package com.production.monoprix.mvp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

abstract class BaseMvpFragment <in V : BaseMvpView, T : BaseMvpPresenter<V>>
: Fragment(), BaseMvpView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter.attachView(this as V)
    }

    override fun getContext(): Context = this.requireActivity()

    protected abstract var mPresenter: T


    override fun showError(error: String?) {
        Toast.makeText(this.context, error, Toast.LENGTH_LONG).show()
    }

    override fun showError(stringResId: Int) {
        Toast.makeText(this.context, stringResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(srtResId: Int) {
        Toast.makeText(this.context, srtResId, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun showDilaogBox(title: String, desc: String){
        AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(desc)
            .setCancelable(false)
            .setPositiveButton("Ok"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .show()
    }
}