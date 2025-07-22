import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.FriendChallengeItem
import android.widget.ImageView
import android.widget.TextView
import com.example.planup.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class FriendChallengeAdapter(private val items: List<FriendChallengeItem>) :
    RecyclerView.Adapter<FriendChallengeAdapter.FriendChallengeViewHolder>() {

    inner class FriendChallengeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.friend_challenge_profile)
        val title = view.findViewById<TextView>(R.id.friend_challenge_title_tv)
        val desc = view.findViewById<TextView>(R.id.friend_challenge_description_tv)
        val pie1 = view.findViewById<PieChart>(R.id.friend_challenge_pc1)
        val pie2 = view.findViewById<PieChart>(R.id.friend_challenge_pc2)
        val pie3 = view.findViewById<PieChart>(R.id.friend_challenge_pc3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendChallengeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_friend_challenge, parent, false)
        return FriendChallengeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendChallengeViewHolder, position: Int) {
        val item = items[position]
        holder.img.setImageResource(item.profileResId)
        holder.title.text = item.name
        holder.desc.text = item.description

        listOf(holder.pie1, holder.pie2, holder.pie3).forEachIndexed { i, pie ->
            setupPieChart(pie, item.pieValues.getOrNull(i) ?: 0f)
        }
    }

    override fun getItemCount() = items.size

    private fun setupPieChart(pie: PieChart, value: Float) {
        pie.setExtraOffsets(-3f,-3f,-3f,-3f)
        pie.setUsePercentValues(false)
        pie.description.isEnabled = false
        pie.setDrawEntryLabels(false)
        pie.setDrawCenterText(true)
        pie.setTouchEnabled(false)
        pie.legend.isEnabled = false
        pie.holeRadius = 85f
        pie.setTransparentCircleAlpha(0)

        val entries = listOf(
            PieEntry(value),
            PieEntry(100 - value)
        )

        val colors = listOf(
            Color.BLUE, Color.LTGRAY
        )

        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            setDrawValues(false)
        }

        pie.data = PieData(dataSet)
        pie.centerText = "${value.toInt()}%"
        pie.invalidate()
    }
}