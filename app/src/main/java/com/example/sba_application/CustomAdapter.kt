//region CustomAdapter.kt

    //TODO: wrtie description of what the adaper does and were its adapted

    //region Packages

        package com.example.sba_application

    //endregion

    //region Imports

        import android.content.Intent
        import android.view.LayoutInflater
        import android.view.View
        import android.view.ViewGroup
        import android.widget.TextView
        import androidx.recyclerview.widget.RecyclerView

    //endregion

    //region Adapter Class

        // borrowed from https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
        class CustomAdapter(private val dataSet: List<HostData>):RecyclerView.Adapter<CustomAdapter.ViewHolder>()
        {
            //region Recycler
                class ViewHolder(view: View):RecyclerView.ViewHolder(view)
                {

                    //region Variables

                    private val textView: TextView = view.findViewById(R.id.textView)

                    //endregion

                    //region Name binder

                    fun bind(host: HostData)
                    {

                        //currently just put the name of the host to be clicked on, idk what other info to put tbh
                        textView.text = "${host.Host_Name} ${host.Host_Surname}"

                        itemView.setOnClickListener {

                            val intent = Intent(itemView.context, DetailedHost::class.java)

                            intent.putExtra("HOST_ID", host.hostId)

                            itemView.context.startActivity(intent)
                        }
                    }

                    //endregion
                }

            //endregion

            //region View maker

                // Create new views (invoked by the layout manager)
                override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder
                {
                    // Create a new view, which defines the UI of the list item
                    val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.text_row_item, viewGroup, false)

                    return ViewHolder(view)
                }

            //endregion

            //region Override Name Binder

                override fun onBindViewHolder(holder: ViewHolder, position: Int)
                {
                    holder.bind(dataSet[position])
                }

            //endregion

            //region Override Item Count

                // Return the size of your dataset (invoked by the layout manager)
                override fun getItemCount() = dataSet.size

            //endregion
        }

    //endregion

//endregion