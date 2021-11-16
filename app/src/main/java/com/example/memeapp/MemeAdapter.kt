package com.example.memeapp

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MemeAdapter(private val listener:OnItemClickListener):RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {
    val list=ArrayList<MemeData>()
    class MemeViewHolder(view: View):RecyclerView.ViewHolder(view){
        val memeView:ImageView=view.findViewById(R.id.meme_view)
        val authorView:TextView=view.findViewById(R.id.author_text_view)
        val subRedditView:TextView=view.findViewById(R.id.reddit_author_view)
        val progressBar:ProgressBar=view.findViewById(R.id.progress_bar)
        val shareMeme:ImageButton=view.findViewById(R.id.share_image_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val item=LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item,parent,false)
        val viewHolder = MemeViewHolder(item)
        item.setOnClickListener{
            listener.onItemClick(list[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val currentItem=list[position]
        holder.progressBar.visibility=View.VISIBLE
        Glide.with(holder.memeView.context)
            .asDrawable()
            .fitCenter()
            .listener(object:RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    holder.memeView.setImageResource(R.drawable.baseline_broken_image_24)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    return false
                }
            }
            )
            .error(R.drawable.baseline_broken_image_24)
            .load(currentItem.getmImageUrl())
            .into(holder.memeView)
        holder.authorView.text=currentItem.getmAuthor()
        holder.subRedditView.text=currentItem.getmRedditSource()
        holder.shareMeme.setOnClickListener {
            listener.shareMeme(currentItem.getmImageUrl())
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateData(data:ArrayList<MemeData>){
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }
    interface OnItemClickListener{
        fun onItemClick(item:MemeData)
        fun shareMeme(str:String)
    }
}
