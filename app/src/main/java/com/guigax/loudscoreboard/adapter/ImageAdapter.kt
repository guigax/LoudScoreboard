import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.guigax.loudscoreboard.R

class ImageAdapter(context: Context, private val images: Array<Int>) :
    ArrayAdapter<Int>(context, 0, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView =
                LayoutInflater.from(context).inflate(R.layout.item_sound_image, parent, false)
        }

        val imageView = itemView!!.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(images[position])

        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
