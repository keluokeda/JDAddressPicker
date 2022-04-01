package com.ke.jdaddresspicker.lib

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ke.jdaddresspicker.lib.databinding.KeJdAddressPickerItemTextBinding
import com.ke.jdaddresspicker.lib.databinding.KeJdAddressPickerLayoutSelectorBinding
import com.ke.mvvm.base.ui.BaseViewBindingAdapter

class AddressPicker internal constructor(private val builder: AddressPickerBuilder) {

    private var bottomSheetDialog: BottomSheetDialog? = null
    private val binding: KeJdAddressPickerLayoutSelectorBinding =
        KeJdAddressPickerLayoutSelectorBinding.inflate(LayoutInflater.from(builder.context))

    private val provider: (Int?) -> List<Pair<Int, String>> = builder.provider


    var onSelectedListener: ((Pair<Int, String>, Pair<Int, String>, Pair<Int, String>) -> Unit)? =
        null


    //选中的省
    private var selectedProvince: Pair<Int, String>? = null

    //选中的市
    private var selectedCity: Pair<Int, String>? = null


    private val adapter =
        object : BaseViewBindingAdapter<Pair<Int, String>, KeJdAddressPickerItemTextBinding>() {
            override fun bindItem(
                item: Pair<Int, String>,
                viewBinding: KeJdAddressPickerItemTextBinding,
                viewType: Int,
                position: Int
            ) {
                viewBinding.name.text = item.second
            }

            override fun createViewBinding(
                inflater: LayoutInflater,
                parent: ViewGroup,
                viewType: Int
            ): KeJdAddressPickerItemTextBinding {
                return KeJdAddressPickerItemTextBinding.inflate(inflater, parent, false)
            }

        }

    init {
        binding.root.setBackgroundColor(builder.backgroundColor)
        binding.title.apply {
            setTextColor(builder.titleColor)
            text = builder.title
        }
        binding.back.setImageResource(builder.closeButtonRes)
        binding.province.setTextColor(builder.underlineColor)
        binding.city.setTextColor(builder.underlineColor)
        binding.town.setTextColor(builder.underlineColor)

        listOf(binding.provinceUnderline, binding.cityUnderline, binding.townUnderline).forEach {
            it.setBackgroundColor(builder.underlineColor)
        }
        binding.divider.setBackgroundColor(builder.dividerColor)




        binding.recyclerView.adapter = adapter


        adapter.setList(provider(null))

        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            if (selectedProvince == null) {
                //选中了省
                setSelectedProvince(item)
            } else if (selectedCity == null) {
                //选中了市
                setSelectedCity(item)
            } else {
//                onSelectedListener?.(selectedProvince, selectedCity, item)
                bottomSheetDialog?.dismiss()
                onSelectedListener?.invoke(selectedProvince!!, selectedCity!!, item)
            }
        }

        binding.province.setOnClickListener {
            if (binding.province.text != builder.idleTitleText) {
                showProvinceList()
            }
        }
        binding.city.setOnClickListener {
            if (binding.province.text != builder.idleTitleText) {
                selectedCity = null
                setSelectedProvince(selectedProvince!!)
            }
        }

        showProvinceList()
    }

    private fun showProvinceList() {

        selectedProvince = null

        binding.province.text = builder.idleTitleText
        binding.provinceUnderline.isVisible = true

        binding.city.isInvisible = true
        binding.cityUnderline.isInvisible = true

        binding.town.isInvisible = true
        binding.townUnderline.isInvisible = true

        adapter.setNewInstance(provider(null).toMutableList())
        binding.recyclerView.scrollToPosition(0)

    }

    private fun setSelectedProvince(pair: Pair<Int, String>) {
        binding.provinceUnderline.isInvisible = true
        binding.province.text = pair.second
        binding.townUnderline.isVisible = false
        binding.town.isVisible = false
        binding.city.isVisible = true
        binding.city.text = builder.idleTitleText
        binding.cityUnderline.isVisible = true
        adapter.setNewInstance(provider(pair.first).toMutableList())
        binding.recyclerView.scrollToPosition(0)

        selectedProvince = pair
    }

    private fun setSelectedCity(pair: Pair<Int, String>) {
        binding.cityUnderline.isInvisible = true
        binding.city.text = pair.second
        binding.town.isVisible = true
        binding.town.text = builder.idleTitleText
        binding.townUnderline.isVisible = true
        adapter.setNewInstance(provider(pair.first).toMutableList())
        binding.recyclerView.scrollToPosition(0)
        selectedCity = pair
    }

    fun show() {

        bottomSheetDialog = BottomSheetDialog(builder.context)
        bottomSheetDialog?.setContentView(binding.root)
        bottomSheetDialog?.show()
    }

}

class AddressPickerBuilder constructor(
    internal val context: Context,
    internal val provider: (Int?) -> List<Pair<Int, String>>
) {
    var title: String = "所在地区"

    @ColorInt
    var titleColor: Int = Color.WHITE

    var idleTitleText = "请选择"


    @DrawableRes
    var closeButtonRes: Int = R.drawable.ke_jd_address_picker_baseline_clear_white_24dp


    @ColorInt
    var backgroundColor: Int = Color.parseColor("#2D0888")

    @ColorInt
    var underlineColor = Color.WHITE

    @ColorInt
    var dividerColor = Color.parseColor("#cccccc")

    @ColorInt
    var defaultNameTextColor = Color.WHITE

    @ColorInt
    var selectedNameTextColor = Color.RED

    fun build(): AddressPicker = AddressPicker(this)


}