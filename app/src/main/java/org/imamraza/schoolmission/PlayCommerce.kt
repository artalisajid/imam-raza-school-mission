package org.imamraza.schoolmission

import android.app.Activity
import androidx.compose.runtime.*
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import java.security.MessageDigest

// Actual prices are always queried from Google Play. Checkout requires verified login
// and the Supabase operator-controlled commerce flag.
class PlayCommerce(private val activity:Activity,private val scope:CoroutineScope) {
 private lateinit var client:BillingClient
 private val products=mutableStateMapOf<String,ProductDetails>()
 private val prices=mutableStateMapOf<String,String>()
 var info by mutableStateOf("Google Play products have not been enabled.")
   private set
 var ready by mutableStateOf(false);private set

 fun initialize() {
  client=BillingClient.newBuilder(activity)
   .setListener(PurchasesUpdatedListener { result,purchases->
    if(result.responseCode==BillingClient.BillingResponseCode.OK && purchases!=null){
     processPurchases(purchases)
    } else if(result.responseCode!=BillingClient.BillingResponseCode.USER_CANCELED){
     info="Google Play purchase update: "+result.debugMessage
    }
   })
   .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
   .enableAutoServiceReconnection().build()
  client.startConnection(object:BillingClientStateListener{
   override fun onBillingSetupFinished(result:BillingResult) {
    if(result.responseCode==BillingClient.BillingResponseCode.OK) {
     ready=true;fetchProducts();restore()
    } else info="Google Play is unavailable: "+result.debugMessage
   }
   override fun onBillingServiceDisconnected() { ready=false;info="Google Play disconnected." }
  })
 }
 private fun fetchProducts(){
  if(!ready)return
  val ids=MissionContent.packs.map { it.id }
  val list=ids.map { sku -> QueryProductDetailsParams.Product.newBuilder()
    .setProductId(sku).setProductType(BillingClient.ProductType.INAPP).build()
  }
  client.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(list).build()) { result,response->
   if(result.responseCode!=BillingClient.BillingResponseCode.OK){
    info="Google Play catalog unavailable: "+result.debugMessage;return@queryProductDetailsAsync
   }
   val details=response.productDetailsList
   details.forEach { p ->
    products[p.productId]=p
    prices[p.productId]=p.oneTimePurchaseOfferDetailsList?.firstOrNull()?.formattedPrice
      ?: p.oneTimePurchaseOfferDetails?.formattedPrice ?: "Price unavailable"
   }
   info=if(details.isEmpty()) "Collections are not yet registered in Google Play Console." else "Google Play catalog loaded."
  }
 }
 fun price(sku:String)=prices[sku] ?: "Price not configured"
 fun canBuy(sku:String,enabled:Boolean)=enabled && ready && products.containsKey(sku)
 fun buy(sku:String,enabled:Boolean){
  if(!enabled){info="Checkout is currently disabled until the school mission is authorized.";return}
  val session=SupporterAuth.session
  if(session==null){info="Sign in with Google before purchasing.";return}
  val details=products[sku]
  if(!ready || details==null){info="This product isn't available on Google Play yet.";return}
  val offer=details.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken
  val param=BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details)
  if(!offer.isNullOrBlank()) param.setOfferToken(offer)
  val account=MessageDigest.getInstance("SHA-256").digest(session.userId.toByteArray()).joinToString(""){"%02x".format(it)}
  val flow=BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(param.build()))
   .setObfuscatedAccountId(account).build()
  val result=client.launchBillingFlow(activity,flow)
  if(result.responseCode!=BillingClient.BillingResponseCode.OK)info=result.debugMessage
 }
 fun restore(){
  if(!ready)return
  client.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()) { result,purchases->
   if(result.responseCode==BillingClient.BillingResponseCode.OK)processPurchases(purchases)
  }
 }
 private fun processPurchases(purchases:List<Purchase>){
  for(purchase in purchases) {
   when(purchase.purchaseState){
    Purchase.PurchaseState.PENDING -> info="Purchase pending payment confirmation."
    Purchase.PurchaseState.PURCHASED -> {
     scope.launch {
      val sku=purchase.products.firstOrNull()
      info=if(sku==null) "Unknown Google Play product."
      else MissionBackend.verify(sku,purchase.purchaseToken)
     }
    }
   }
  }
 }
 fun dispose(){if(::client.isInitialized)client.endConnection()}
}
