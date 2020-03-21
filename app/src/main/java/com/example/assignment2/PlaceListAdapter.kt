package com.example.assignment2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2.model.PlaceData
import kotlinx.android.synthetic.main.place_list_row.view.*

class PlaceListAdapter(private val context: Context, private val placeDataList: List<PlaceData>) :
    RecyclerView.Adapter<PlaceListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.place_list_row, p0, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return placeDataList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        val placeData: PlaceData = placeDataList[p1]
        p0.placeName.text = placeData.name
        if (placeData.distance != null) {
            p0.distance.text = (placeData.distance / 1000).toString() + " kms"
        }
        p0.row.setOnClickListener {
            DataHandler.getInstance().selectedPlace = placeData
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIcon: ImageView = view.placeImage
        val placeName: TextView = view.tvPlaceName
        val distance: TextView = view.tvDistance
        val row: CardView = view.rowLL
    }
}


