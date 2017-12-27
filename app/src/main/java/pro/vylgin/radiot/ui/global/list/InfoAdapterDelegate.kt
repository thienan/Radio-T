package pro.vylgin.radiot.ui.global.list

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_news.view.*
import pro.vylgin.radiot.R
import pro.vylgin.radiot.entity.Entry
import pro.vylgin.radiot.extension.getTransitionNames
import pro.vylgin.radiot.extension.humanTime
import pro.vylgin.radiot.extension.inflate

class InfoAdapterDelegate(private val clickListener: (EntrySharedElement) -> Unit) : AdapterDelegate<MutableList<ListItem>>() {

    override fun isForViewType(items: MutableList<ListItem>, position: Int) =
            items[position] is ListItem.InfoItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            PodcastViewHolder(parent.inflate(R.layout.item_info), clickListener)

    override fun onBindViewHolder(items: MutableList<ListItem>,
                                  position: Int,
                                  viewHolder: RecyclerView.ViewHolder,
                                  payloads: MutableList<Any>) =
            (viewHolder as PodcastViewHolder).bind((items[position] as ListItem.InfoItem).entry)

    private class PodcastViewHolder(val view: View, clickListener: (EntrySharedElement) -> Unit) : RecyclerView.ViewHolder(view) {
        private lateinit var info: Entry

        init {
            view.setOnClickListener {
                val entrySharedElement = EntrySharedElement(info, null, view.titleTV, view.dateTV)
                clickListener.invoke(entrySharedElement)
            }
        }

        fun bind(info: Entry) {
            this.info = info

            val (titleTransitionName, dateTransitionName) = info.getTransitionNames()
            ViewCompat.setTransitionName(view.titleTV, titleTransitionName)
            ViewCompat.setTransitionName(view.dateTV, dateTransitionName)

            view.titleTV.text = info.title
            view.showNotesTV.text = info.showNotes
            view.dateTV.text = info.date.humanTime()
        }
    }
}