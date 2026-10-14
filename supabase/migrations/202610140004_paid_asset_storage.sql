
insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types)
 values ('paid-sticker-packs','paid-sticker-packs',false,25000000,array['application/zip'])
 on conflict(id) do nothing;
create policy "Only owners can read paid sticker packs"
 on storage.objects for select to authenticated
 using (
   bucket_id='paid-sticker-packs'
   and exists (
     select 1 from public.entitlements e
     where e.user_id=(select auth.uid())
       and e.product_id=split_part(storage.objects.name,'/',1)
       and e.revoked_at is null
   )
 );
