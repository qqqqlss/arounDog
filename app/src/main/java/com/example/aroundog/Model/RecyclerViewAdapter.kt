import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.aroundog.BuildConfig
import com.example.aroundog.Model.RecyclerViewItem
import com.example.aroundog.Model.UpdateWalkHistory
import com.example.aroundog.R
import com.example.aroundog.Service.RetrofitService
import com.example.aroundog.Service.WalkService
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//package com.example.aroundog.Model
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.recyclerview.widget.RecyclerView
//import com.example.aroundog.BuildConfig
//import com.example.aroundog.R
//import com.example.aroundog.Service.RetrofitService
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class RecyclerViewAdapter(private val data : ArrayList<RecyclerViewItem> ):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
//    var serialNumber:Int = 0
//
//
//    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
//        var imageView:ImageView
//        var textViewGood:TextView
//        var textViewBad:TextView
//        var textViewId:TextView
//        var buttonGood:ImageButton
//        var buttonBad:ImageButton
//        var textViewSerialNumber:TextView
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BuildConfig.SERVER)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val retrofitAPI = retrofit.create(RetrofitService::class.java)
//        init {
//            var clickGood:Boolean = false
//            var clickBad:Boolean = false
//
//            textViewId = view.findViewById(R.id.textViewId)
//            imageView = view.findViewById(R.id.itemImage)
//            textViewGood = view.findViewById(R.id.textViewGood)
//            textViewBad = view.findViewById(R.id.textViewBad)
//            buttonGood= view.findViewById(R.id.buttonGood)
//            buttonBad= view.findViewById(R.id.buttonBad)
//            textViewSerialNumber = view.findViewById(R.id.textViewSerialNumber)
//
//            buttonGood.setOnClickListener(View.OnClickListener {
//                if(!clickGood){//기존에 좋아요버튼을 누르지 않았을때 => 어플 껐다 키면 쓸모없어짐
//                    sendDB("good", textViewSerialNumber.text.toString())
//                    textViewGood.text = (textViewGood.text.toString().toInt() + 1).toString()
//                    clickGood=true
//                }
//                else{
//                    Toast.makeText(view.context, "좋아요는 한번만 누를 수 있습니다!", Toast.LENGTH_SHORT).show()
//                }
//            })
//            buttonBad.setOnClickListener(View.OnClickListener {
//                if(!clickBad){//기존에 싫어요버튼을 누르지 않았을때
//                    sendDB("bad", textViewSerialNumber.text.toString())
//                    textViewBad.text = (textViewBad.text.toString().toInt() - 1).toString()
//                    clickBad=true
//                }
//                else{
//                    Toast.makeText(view.context, "싫어요는 한번만 누를 수 있습니다!", Toast.LENGTH_SHORT).show()
//                }
//            })
//
//        }
//        fun sendDB(button:String, serialNumber:String) {
//            retrofitAPI.clickButton(button, serialNumber).enqueue(object : Callback<UpdateWalkHistory> {
//                override fun onResponse(
//                    call: Call<UpdateWalkHistory>,
//                    response: Response<UpdateWalkHistory>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d("sex", "hihi" + response.body()!!.result)
//
//                    }
//                }
//
//                override fun onFailure(call: Call<UpdateWalkHistory>, t: Throwable) {
//
//                }
//            })
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        var context:Context = parent.context
//
//        var inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//        var view:View = inflater.inflate(R.layout.recycler_item,parent, false)
////        var vh:RecyclerViewAdapter.ViewHolder = RecyclerViewAdapter.ViewHolder(view)
////        return vh
//        when (viewType) {
//            0 -> {
//                return
//            }
//
//        }
//
//
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var item = data.get(position)//넘어온
//        holder.textViewId.text = item.userId
//        holder.imageView.setImageBitmap(item.img)
//        holder.textViewGood.text = item.good.toString()
//        holder.textViewBad.text = item.bad.toString()
//        holder.textViewSerialNumber.text = item.serialNumber
//    }
//
//    override fun getItemCount(): Int {
//        return data.size
//    }
//}

class RecyclerViewAdapter(private val data : ArrayList<RecyclerViewItem?> ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val TAG = "RECYCLERVIEWADAPTER"
    private val VIEW_TYPE_ITEM=0;
    private val VIEW_TYPE_LOADING = 1


    class ItemViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        private val TAG = "RECYCLERVIEWADAPTER"
        var textViewUserId: TextView
        var textViewWalkId: TextView
        var imageView: ImageView
        var textViewGood: TextView
        var textViewBad: TextView
        var buttonGood: ImageButton
        var buttonBad: ImageButton
        var textViewWalkSecond:TextView
        var clickGood: Boolean = false
        var clickBad: Boolean = false

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WalkService::class.java)

        init {
            textViewUserId = view.findViewById(R.id.textViewUserId)
            textViewWalkId = view.findViewById(R.id.textViewWalkId)
            imageView = view.findViewById(R.id.itemImage)
            textViewGood = view.findViewById(R.id.textViewGood)
            textViewBad = view.findViewById(R.id.textViewBad)
            buttonGood = view.findViewById(R.id.buttonGood)
            buttonBad = view.findViewById(R.id.buttonBad)
            textViewWalkSecond = view.findViewById(R.id.textViewWalkSecond)

            buttonGood.setOnClickListener(View.OnClickListener {
                if (!clickGood) {//기존에 좋아요버튼을 누르지 않았을때 => 어플 껐다 키면 쓸모없어짐
                    sendDB(textViewWalkId.text.toString().toLong(), "good")
                    textViewGood.text = (textViewGood.text.toString().toInt() + 1).toString()
                    clickGood = true
                } else {
                    Toast.makeText(view.context, "좋아요는 한번만 누를 수 있습니다!", Toast.LENGTH_SHORT).show()
                }
            })
            buttonBad.setOnClickListener(View.OnClickListener {
                if (!clickBad) {//기존에 싫어요버튼을 누르지 않았을때
                    sendDB(textViewWalkId.text.toString().toLong(), "bad")
                    textViewBad.text = (textViewBad.text.toString().toInt() - 1).toString()
                    clickBad = true
                } else {
                    Toast.makeText(view.context, "싫어요는 한번만 누를 수 있습니다!", Toast.LENGTH_SHORT).show()
                }
            })

        }

        fun sendDB(walkId:Long, button: String) {
            retrofit.clickButton(walkId, button)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "성공")
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {

                    }
                })

        }


        fun setItems(viewItem: RecyclerViewItem){
            this.textViewUserId.text = viewItem.userId
            this.textViewWalkId.text = viewItem.id.toString()
            this.imageView.setImageBitmap(viewItem.img)
            this.textViewGood.text = viewItem.good.toString()
            var bad:String
            if (viewItem.bad == 0) {
                bad = viewItem.bad.toString()
            } else {
                bad = "-" + viewItem.bad.toString()
            }
            this.textViewBad.text = bad //마이너스 추가


            var totalSecond = viewItem.walkSecond
            var hour = TimeUnit.SECONDS.toHours(totalSecond)
            var minute = TimeUnit.SECONDS.toMinutes(totalSecond) - hour * 60
            var second = TimeUnit.SECONDS.toSeconds(totalSecond) - hour * 3600 - minute * 60
            var strTime = "소요시간 : " + String.format("%02d", hour) + " 시 " + String.format("%02d",minute) + " 분 " + String.format("%02d", second) + "초"
            this.textViewWalkSecond.text =strTime
        }
    }
    class LoadingViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        init {
            var progressBar = itemView.findViewById<ProgressBar>(R.id.loading_progressBar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM)
            return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
            )
        else
            return LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_loading, parent, false)
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.setItems(data[position]!!)
        } else {

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }



    override fun getItemViewType(position: Int): Int {
        if (data.get(position) == null) {
            return VIEW_TYPE_LOADING
        }
        return VIEW_TYPE_ITEM
    }
}