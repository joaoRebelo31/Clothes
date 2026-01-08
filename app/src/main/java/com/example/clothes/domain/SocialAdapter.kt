
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.clothes.R
import com.example.clothes.databinding.ItemPostBinding
import com.example.clothes.domain.ClosetAdapter
import com.example.clothes.model.Cloth
import com.example.clothes.model.Post

class SocialAdapter(
    private val context: Context,
    private val postsList: List<Post>,
    private val itemClickedListener: OnItemClickListener?
) : RecyclerView.Adapter<SocialAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun invokeAccount(profileName: String)
        fun savePost(post: Post)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postItemBinding = ItemPostBinding.bind(itemView)

        fun bindView(post: Post, itemClickedListener: OnItemClickListener?) {
            postItemBinding.saveButton.setOnClickListener{
                itemClickedListener?.savePost(post)
            }
            postItemBinding.profileImage.setOnClickListener{
                itemClickedListener?.invokeAccount(post.profileUsername!!)
            }
            Glide.with(context).load(post.profileImage).into(postItemBinding.profileImage)
            postItemBinding.profileName.text = post.profileUsername
            postItemBinding.imagesCarrousel.adapter = PostImageAdapter(context, post)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = postsList[position]
        holder.bindView(post, itemClickedListener)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }
}