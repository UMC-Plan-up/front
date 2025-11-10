package com.example.planup.main.friend.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.friend.ui.FriendFragment
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel


/**
 * FriendFragment 하위에서 하위 탭으로 이동되었을떄(depth2일때 공통 Fragment)
 */
abstract class FriendDepth2Fragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : FriendDepth2FragmentBase() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!


    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate(inflater, container, false)
        return binding.root
    }


    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

abstract class FriendDepth2FragmentBase() : Fragment() {

    protected val friendViewModel: FriendViewModel by activityViewModels()
    protected val mainSnackbarViewModel: MainSnackbarViewModel by activityViewModels()


    protected fun goToFriendMain() {
        parentFragmentManager.popBackStack(
            FriendFragment.FRIEND_FRAGMENT_STACK,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}