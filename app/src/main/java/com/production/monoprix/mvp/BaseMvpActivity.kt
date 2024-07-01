package com.production.monoprix.mvp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.production.monoprix.MyApplication

abstract class BaseMvpActivity<in V : BaseMvpView, T : BaseMvpPresenter<V>>
    : AppCompatActivity(), BaseMvpView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter.attachView(this as V)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun getContext(): Context = this

    protected abstract var mPresenter: T


    override fun showError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun showError(stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(srtResId: Int) {
        Toast.makeText(this, srtResId, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun showDilaogBox(
        title: String,
        desc: String
    ) {

        try {

            AlertDialog.Builder(applicationContext)
                .setTitle(title)
                .setMessage(desc)
                .setCancelable(false)
                .setPositiveButton(
                    "Ok"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .show()
            /*  AlertDialog.Builder(applicationContext)
                  .setTitle(title)
                  .setMessage(desc)
                  .setCancelable(false)
                  .setPositiveButton("Ok") { dialogInterface, i -> dialogInterface.dismiss() }
                  .show()*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}