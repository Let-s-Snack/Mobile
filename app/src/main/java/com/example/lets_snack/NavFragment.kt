package com.example.lets_snack

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lets_snack.databinding.FragmentMenuBinding

class NavFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatIaBtn = binding.iconChatIa
        val homeBtn = binding.iconHome
        val searchBtn = binding.iconSearch
        val hearthBtn = binding.iconHearth
        val userBtn = binding.iconUser
        val chatIaText = binding.iconChatIaText
        val chatHomeText = binding.iconHomeText
        val chatSearchText = binding.iconSearchText
        val chatHearthText = binding.iconHearthText
        val chatUserText = binding.iconUserText
        val listItens: MutableList<ImageView> = mutableListOf()
        val listNames: MutableList<TextView> = mutableListOf()

        fun animateTranslationY(view: View, startValue: Float, endValue: Float) {
            val animator = ObjectAnimator.ofFloat(view, "translationY", startValue, endValue)
            animator.duration = 300 // Duration of the animation
            animator.start()
        }

        fun updateUI(selectedBtn: ImageView, selectedText: TextView, otherBtns: List<ImageView>, otherTexts: List<TextView>) {
            // Reset all other buttons and texts
            otherBtns.forEach { btn ->
                btn.setImageResource(getDefaultIcon(btn))
                animateTranslationY(btn, btn.translationY, 0f)
            }
            otherTexts.forEach { text ->
                text.visibility = View.INVISIBLE
                animateTranslationY(text, text.translationY, 0f)
            }

            // Apply animation and margin to the selected button and text
            animateTranslationY(selectedBtn, selectedBtn.translationY, -14f)
            selectedText.visibility = View.VISIBLE
            animateTranslationY(selectedText, selectedText.translationY, -14f)
            selectedBtn.setImageResource(getSelectedIcon(selectedBtn))
        }

        chatIaBtn.setOnClickListener {
            listItens.addAll(listOf(homeBtn, searchBtn, hearthBtn, userBtn))
            listNames.addAll(listOf(chatHomeText, chatSearchText, chatHearthText, chatUserText))
            updateUI(chatIaBtn, chatIaText, listItens, listNames)
        }

        homeBtn.setOnClickListener {
            listItens.addAll(listOf(chatIaBtn, searchBtn, hearthBtn, userBtn))
            listNames.addAll(listOf(chatIaText, chatSearchText, chatHearthText, chatUserText))
            updateUI(homeBtn, chatHomeText, listItens, listNames)
        }

        searchBtn.setOnClickListener {
            listItens.addAll(listOf(homeBtn, chatIaBtn, hearthBtn, userBtn))
            listNames.addAll(listOf(chatHomeText, chatIaText, chatHearthText, chatUserText))
            updateUI(searchBtn, chatSearchText, listItens, listNames)
        }

        hearthBtn.setOnClickListener {
            listItens.addAll(listOf(homeBtn, searchBtn, chatIaBtn, userBtn))
            listNames.addAll(listOf(chatHomeText, chatSearchText, chatIaText, chatUserText))
            updateUI(hearthBtn, chatHearthText, listItens, listNames)
        }

        userBtn.setOnClickListener {
            listItens.addAll(listOf(homeBtn, searchBtn, hearthBtn, chatIaBtn))
            listNames.addAll(listOf(chatHomeText, chatSearchText, chatHearthText, chatIaText))
            updateUI(userBtn, chatUserText, listItens, listNames)
        }
    }

    private fun getDefaultIcon(button: ImageView): Int {
        return when (button) {
            binding.iconChatIa -> R.drawable.icon_chat_ia
            binding.iconHome -> R.drawable.icon_home
            binding.iconSearch -> R.drawable.icon_search
            binding.iconHearth -> R.drawable.icon_hearth
            binding.iconUser -> R.drawable.icon_user
            else -> R.drawable.icon_chat_ia // Default case
        }
    }

    private fun getSelectedIcon(button: ImageView): Int {
        return when (button) {
            binding.iconChatIa -> R.drawable.icon_chat_ia_selected2
            binding.iconHome -> R.drawable.icon_home_selected
            binding.iconSearch -> R.drawable.icon_search_selected
            binding.iconHearth -> R.drawable.icon_hearth_selected
            binding.iconUser -> R.drawable.icon_user_selected
            else -> R.drawable.icon_chat_ia_selected // Default case
        }
    }
}
