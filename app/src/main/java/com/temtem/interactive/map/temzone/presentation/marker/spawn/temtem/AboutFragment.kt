package com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.di.GlideApp
import com.temtem.interactive.map.temzone.databinding.AboutFragmentBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn

class AboutFragment(
    private val spawn: Spawn,
) : Fragment(R.layout.about_fragment) {

    private val viewBinding: AboutFragmentBinding by viewBindings()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.descriptionTextView.text = spawn.temtem.description
        viewBinding.heightTextView.text = resources.getString(
            R.string.height_template,
            spawn.temtem.height.cm,
            spawn.temtem.height.inches,
        )
        viewBinding.weightTextView.text = resources.getString(
            R.string.weight_template,
            spawn.temtem.weight.kg,
            spawn.temtem.weight.lbs,
        )
        if (spawn.temtem.gender == null) {
            viewBinding.genderTextView.text = "N/A"
        } else {
            viewBinding.genderTextView.text = resources.getString(
                R.string.gender_template,
                spawn.temtem.gender.male,
                spawn.temtem.gender.female,
            )
        }
        viewBinding.catchRateTextView.text = spawn.temtem.catchRate.toString()
        viewBinding.tvYieldTextView.text = listOf(
            Pair(
                spawn.temtem.tvs.hp,
                resources.getString(R.string.hp_tv_yield_template, spawn.temtem.tvs.hp),
            ),
            Pair(
                spawn.temtem.tvs.sta,
                resources.getString(R.string.sta_tv_yield_template, spawn.temtem.tvs.sta),
            ),
            Pair(
                spawn.temtem.tvs.spd,
                resources.getString(R.string.spd_tv_yield_template, spawn.temtem.tvs.spd),
            ),
            Pair(
                spawn.temtem.tvs.atk,
                resources.getString(R.string.atk_tv_yield_template, spawn.temtem.tvs.atk),
            ),
            Pair(
                spawn.temtem.tvs.def,
                resources.getString(R.string.def_tv_yield_template, spawn.temtem.tvs.def),
            ),
            Pair(
                spawn.temtem.tvs.spatk,
                resources.getString(R.string.spatk_tv_yield_template, spawn.temtem.tvs.spatk),
            ),
            Pair(
                spawn.temtem.tvs.spdef,
                resources.getString(R.string.spdef_tv_yield_template, spawn.temtem.tvs.spdef),
            ),
        )
            .filter { it.first != 0 }
            .joinToString(", ") { it.second }
        viewBinding.primaryTraitTextView.text = resources.getString(
            R.string.trait_template,
            spawn.temtem.traits.first().name,
            spawn.temtem.traits.first().description,
        )
        viewBinding.secondaryTraitTextView.text = resources.getString(
            R.string.trait_template,
            spawn.temtem.traits.last().name,
            spawn.temtem.traits.last().description,
        )
        if (spawn.condition != null) {
            viewBinding.conditionTextView.text = spawn.condition
        } else {
            viewBinding.conditionTextView.visibility = View.GONE
        }
        viewBinding.rateTextView.text = resources.getString(
            R.string.rate_template,
            spawn.rate.first(),
        ) + if (spawn.rate.size > 1)
            spawn.rate.subList(1, spawn.rate.size).joinToString(", ", " (", ")") {
                resources.getString(
                    R.string.rate_template,
                    it,
                )
            } else ""
        viewBinding.levelTextView.text = resources.getString(
            R.string.level_template,
            spawn.level.min,
            spawn.level.max,
        )
        GlideApp.with(requireContext())
            .load(spawn.imageUrl)
            .into(viewBinding.locationImageView)
    }
}
