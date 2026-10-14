
create or replace function public.finalize_verified_google_purchase(
  p_user_id uuid, p_product_id text, p_token_hash text, p_order_reference text
) returns jsonb language plpgsql security definer set search_path=''
as $body$
declare v_purchase_id uuid; v_owner uuid; v_existing text; v_owned uuid;
begin
 if length(p_token_hash) != 64 or p_token_hash !~ '^[0-9a-f]{64}$' then
   raise exception 'Invalid token hash';
 end if;
 if p_order_reference is null or length(p_order_reference) < 3 then
   raise exception 'Verified order reference required';
 end if;
 if not exists (select 1 from public.sticker_products
   where id=p_product_id and play_product_id=p_product_id and published=true) then
   raise exception 'Unpublished or unknown product';
 end if;
 select p.id,p.user_id,p.product_id into v_purchase_id,v_owner,v_existing
 from public.purchase_tokens t join public.purchases p on p.id=t.purchase_id
 where t.token_sha256=p_token_hash for update of p;
 if v_purchase_id is not null then
   if v_owner!=p_user_id or v_existing!=p_product_id then
     raise exception 'Token previously linked to another account/product';
   end if;
   return jsonb_build_object('status','already_verified','purchase_id',v_purchase_id);
 end if;
 select purchase_id into v_owned from public.entitlements
 where user_id=p_user_id and product_id=p_product_id and revoked_at is null;
 if v_owned is not null then
   raise exception 'Product already owned';
 end if;
 insert into public.purchases(user_id,product_id,order_reference,status,verified_at)
 values(p_user_id,p_product_id,p_order_reference,'verified',now())
 returning id into v_purchase_id;
 insert into public.purchase_tokens(purchase_id,token_sha256)
 values(v_purchase_id,p_token_hash);
 insert into public.entitlements(user_id,product_id,purchase_id,revoked_at)
 values(p_user_id,p_product_id,v_purchase_id,null)
 on conflict(user_id,product_id) do update
 set purchase_id=excluded.purchase_id, revoked_at=null, granted_at=now();
 return jsonb_build_object('status','verified','purchase_id',v_purchase_id);
end;
$body$;
revoke all on function public.finalize_verified_google_purchase(uuid,text,text,text)
 from public,anon,authenticated;
grant execute on function public.finalize_verified_google_purchase(uuid,text,text,text)
 to service_role;

create or replace function public.revoke_verified_google_purchase(
 p_token_hash text
) returns boolean language plpgsql security definer set search_path=''
as $body$
declare v_id uuid;
begin
 select t.purchase_id into v_id from public.purchase_tokens t
 where t.token_sha256=p_token_hash;
 if v_id is null then return false; end if;
 update public.purchases set status='refunded' where id=v_id and status='verified';
 update public.entitlements set revoked_at=now()
 where purchase_id=v_id and revoked_at is null;
 return true;
end;
$body$;
revoke all on function public.revoke_verified_google_purchase(text)
 from public,anon,authenticated;
grant execute on function public.revoke_verified_google_purchase(text) to service_role;
