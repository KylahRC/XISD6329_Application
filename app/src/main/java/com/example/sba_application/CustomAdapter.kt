package com.example.sba_application

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// borrowed from https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
class CustomAdapter(
    private val dataSet: List<HostData>
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textView: TextView =
            view.findViewById(R.id.textView)

        fun bind(host: HostData) {

            textView.text =
                "${host.Host_Name} ${host.Host_Surname}"

            itemView.setOnClickListener {

                val intent = Intent(
                    itemView.context,
                    DetailedHost::class.java
                )

                intent.putExtra(
                    "HOST_ID",
                    host.hostId
                )

                itemView.context.startActivity(intent)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(
            dataSet[position]
        )
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}
