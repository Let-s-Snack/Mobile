package com.example.lets_snack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lets_snack.databinding.FragmentMenuBinding

class NavFragment: Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
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

        chatIaBtn.setOnClickListener {
            userBtn.setImageResource(R.drawable.icon_user)
            hearthBtn.setImageResource(R.drawable.icon_hearth)
            chatIaBtn.setImageResource(R.drawable.icon_chat_ia_selected)
        }

        homeBtn.setOnClickListener {
            chatIaBtn.setImageResource(R.drawable.icon_chat_ia)
            userBtn.setImageResource(R.drawable.icon_user)
            hearthBtn.setImageResource(R.drawable.icon_hearth)
        }

        searchBtn.setOnClickListener {
            chatIaBtn.setImageResource(R.drawable.icon_chat_ia)
            userBtn.setImageResource(R.drawable.icon_user)
            hearthBtn.setImageResource(R.drawable.icon_hearth)
        }

        hearthBtn.setOnClickListener {
            chatIaBtn.setImageResource(R.drawable.icon_chat_ia)
            userBtn.setImageResource(R.drawable.icon_user)
            hearthBtn.setImageResource(R.drawable.icon_hearth_selected)
        }

        userBtn.setOnClickListener {
            chatIaBtn.setImageResource(R.drawable.icon_chat_ia)
            hearthBtn.setImageResource(R.drawable.icon_hearth)
            userBtn.setImageResource(R.drawable.icon_user_selected)
        }
    }
}