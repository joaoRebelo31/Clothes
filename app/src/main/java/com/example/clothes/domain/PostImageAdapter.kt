
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothes.R
import com.example.clothes.databinding.ItemPostBinding
import com.example.clothes.databinding.ItemPostImageBinding
import com.example.clothes.domain.ClosetAdapter
import com.example.clothes.model.Cloth
import com.example.clothes.model.Post

class PostImageAdapter(
    private val context: Context,
    private val post: Post
) : RecyclerView.Adapter<PostImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postItemBinding = ItemPostImageBinding.bind(itemView)

        fun bindView(position: Int) {
            var imageUrl: String = ""

            if(position == 0){
                imageUrl = post.image!!
                postItemBinding.infoFrame.visibility = View.GONE
            }
            else if(position == 1){
                imageUrl = post.piece1?.image.toString()
                postItemBinding.typeText.text = post.piece1?.type
                postItemBinding.brandText.text = post.piece1?.brand
                postItemBinding.colorText.text = post.piece1?.color
                postItemBinding.sizeText.text = post.piece1?.size
            }
            else if(position == 2){
                imageUrl = post.piece2?.image.toString()
                postItemBinding.typeText.text = post.piece2?.type
                postItemBinding.brandText.text = post.piece2?.brand
                postItemBinding.colorText.text = post.piece2?.color
                postItemBinding.sizeText.text = post.piece2?.size
            }
            Glide.with(context).load(imageUrl).into(postItemBinding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_post_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun getItemCount(): Int {
        return 3
    }
}