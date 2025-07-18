package com.geekneuron.lookatme

import android.graphics.Typeface
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geekneuron.lookatme.databinding.ItemFontChoiceBinding
import com.geekneuron.lookatme.databinding.ItemImageChoiceBinding
import com.geekneuron.lookatme.databinding.ItemSoundChoiceBinding
import java.io.File

class AssetAdapter(
    private val assetType: AssetType,
    private val onAssetSelected: (AppAsset) -> Unit,
    private val onAssetPlay: (AppAsset) -> Unit,
    private val onAssetDelete: (AppAsset) -> Unit
) : ListAdapter<AppAsset, RecyclerView.ViewHolder>(AssetDiffCallback()) {

    // --- ViewHolders for each asset type ---
    class FontViewHolder(private val binding: ItemFontChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asset: AppAsset, onAssetSelected: (AppAsset) -> Unit, onAssetDelete: (AppAsset) -> Unit) {
            binding.fontNamePreview.text = asset.name
            val typeface = when (asset) {
                is AppAsset.PackagedAsset -> ResourcesCompat.getFont(itemView.context, asset.resourceId)
                is AppAsset.UserAsset -> Typeface.createFromFile(File(asset.filePath))
            }
            binding.fontNamePreview.typeface = typeface
            binding.deleteButton.visibility = if (asset is AppAsset.UserAsset) View.VISIBLE else View.GONE
            binding.deleteButton.setOnClickListener { onAssetDelete(asset) }
            itemView.setOnClickListener { onAssetSelected(asset) }
        }
    }

    class ImageViewHolder(private val binding: ItemImageChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asset: AppAsset, onAssetSelected: (AppAsset) -> Unit, onAssetDelete: (AppAsset) -> Unit) {
            binding.imageName.text = asset.name
            val loadTarget = when (asset) {
                is AppAsset.PackagedAsset -> asset.resourceId
                is AppAsset.UserAsset -> File(asset.filePath)
            }
            Glide.with(itemView.context).load(loadTarget).into(binding.imagePreview)
            binding.deleteButton.visibility = if (asset is AppAsset.UserAsset) View.VISIBLE else View.GONE
            binding.deleteButton.setOnClickListener { onAssetDelete(asset) }
            itemView.setOnClickListener { onAssetSelected(asset) }
        }
    }

    class SoundViewHolder(private val binding: ItemSoundChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asset: AppAsset, onAssetSelected: (AppAsset) -> Unit, onAssetPlay: (AppAsset) -> Unit, onAssetDelete: (AppAsset) -> Unit) {
            binding.soundName.text = asset.name
            binding.deleteButton.visibility = if (asset is AppAsset.UserAsset) View.VISIBLE else View.GONE
            binding.deleteButton.setOnClickListener { onAssetDelete(asset) }
            binding.playButton.setOnClickListener { onAssetPlay(asset) }
            itemView.setOnClickListener { onAssetSelected(asset) }
        }
    }

    // --- Core Adapter Methods ---
    override fun getItemViewType(position: Int): Int = assetType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (AssetType.values()[viewType]) {
            AssetType.FONTS -> FontViewHolder(ItemFontChoiceBinding.inflate(inflater, parent, false))
            AssetType.IMAGES -> ImageViewHolder(ItemImageChoiceChoiceBinding.inflate(inflater, parent, false))
            AssetType.SOUNDS -> SoundViewHolder(ItemSoundChoiceBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val asset = getItem(position)
        when (holder) {
            is FontViewHolder -> holder.bind(asset, onAssetSelected, onAssetDelete)
            is ImageViewHolder -> holder.bind(asset, onAssetSelected, onAssetDelete)
            is SoundViewHolder -> holder.bind(asset, onAssetSelected, onAssetPlay, onAssetDelete)
        }
    }
}

class AssetDiffCallback : DiffUtil.ItemCallback<AppAsset>() {
    override fun areItemsTheSame(oldItem: AppAsset, newItem: AppAsset): Boolean {
        return oldItem.name == newItem.name && oldItem.javaClass == newItem.javaClass
    }
    override fun areContentsTheSame(oldItem: AppAsset, newItem: AppAsset): Boolean {
        return oldItem == newItem
    }
}
