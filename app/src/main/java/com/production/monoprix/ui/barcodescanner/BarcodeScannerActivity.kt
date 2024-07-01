package com.production.monoprix.ui.barcodescanner

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.toSpanned
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.production.monoprix.MyApplication
import com.production.monoprix.R
import com.production.monoprix.api.ErrorBody
import com.production.monoprix.model.*
import com.production.monoprix.mvp.BaseMvpActivity
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.ui.checkout.CheckoutActivity
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.dialog_barcode.view.*
import kotlinx.android.synthetic.main.dialog_info.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.layout_scan_cart_items.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketException
import java.util.*


class BarcodeScannerActivity :
    BaseMvpActivity<BarcodeContractor.View, BarcodeContractor.Presenter>(),
    BarcodeContractor.View, View.OnClickListener {

    lateinit var sessionManager: SessionManager
    override var mPresenter: BarcodeContractor.Presenter = BarcodePresenter()

    /*scan barcode*/
    lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource
    lateinit var barcodeResult: String
    lateinit var validBC: String
    lateinit var title: String
    private var mFullDatum = ArrayList<ProductByCodeModel>()
    lateinit var linearLayoutManager: LinearLayoutManager
    var detailsAdapter: ScanProductDetailsAdapter? = null
    lateinit var jsonArray: JSONArray
    lateinit var jsonobject: JSONObject
    var filtercustomDialog: Dialog? = null
    lateinit var location: String

    //    lateinit var localRes: StatusProductByCodeModel
    /*database*/
    lateinit var db: AppDatabase

    // private var mDbWorkerThread: DbWorkerThread? = null
    private val mUiHandler = Handler()
    var pluCode: String = ""
    var quantity: Int = 0
    var position: Int = 0
    private var iconHeight: Int = 0
    var isSelsectedLanguage: Boolean = false
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    var timeIndex = 10L
    var dialogstatus = false
    var apiresponseStatus = false


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MyApplication.localeManager?.setLocale(base!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        sessionManager = SessionManager(this)
        init()
    }

    fun init() {

        iconHeight = resources.getDimension(R.dimen.thirty).toInt()
        var param: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 100)
        title = intent.getStringExtra("title")!!
        location = intent.getStringExtra("location")!!
//        location = "25.285430908203125,51.52846908569336"//Jaideep
        Log.i("latlong", location)
        txt_title.text = title
        sessionManager.barcode = ""
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_scan_cart_lits.layoutManager = linearLayoutManager
        recycler_scan_cart_lits.isNestedScrollingEnabled = true
        /*set onclick listener*/
        img_left_arrow.setOnClickListener(this)
        rele_scan_pay.setOnClickListener(this)
        txt_info.setOnClickListener(this)
        edt_enter_barcode.setOnClickListener(this)
        //  createCameraFocus()
        // addBarcodeReaderFragment()
        db = AppDatabase(this)
        fetchWeatherDataFromDb()
        /* edt_enter_barcode.setOnEditorActionListener { v, actionId, event ->
             var handled = false
             if (actionId == EditorInfo.IME_ACTION_SEND) {
                 val barcode = edt_enter_barcode.text.toString()
                 showDetails(barcode)
                 edt_enter_barcode.text.clear()
                 edt_enter_barcode.isClickable = false
                 handled = true
             }
             handled
         }*/
        isSelsectedLanguage = sessionManager.languageselect == "en"


    }

    override fun onResume() {
        super.onResume()
        timeIndex = 10L
        handler = Handler()
        runnable = Runnable {
            timeIndex++
//            Log.e("second====", "${timeIndex}")
            handler.postDelayed(runnable, 1000) // 1 second
        }

        handler.post(runnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null);
    }

    fun fetchWeatherDataFromDb() {


        //   val task = Runnable {
        val productsData =
            db.todoDao().getAll()
        //     mUiHandler.post {
        recycler_scan_cart_lits.visibility = View.VISIBLE
        sessionManager.totalCart = productsData.size.toString()
        if (!TextUtils.isEmpty(sessionManager.totalCart)) {
            txt_cart_count.text = sessionManager.totalCart
        } else {
            txt_cart_count.text = "0"
        }
        if (productsData.isNotEmpty()) {
            txt_cart_count.visibility = View.VISIBLE
            // Temp Code added by hm for hot fix
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            var width = displayMetrics.widthPixels
            var height = displayMetrics.heightPixels
            //Log.i("height","is"+height)
            var layout_height = 550
            if (height > 1344) {
                layout_height = 1000
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 50)
            cart_include.layoutParams = params
            (scan_layout.layoutParams as RelativeLayout.LayoutParams).height = layout_height
            //Temp code added  by hm for hot fix
            scan_preview.visibility = View.GONE
            scan_preview_small.visibility = View.VISIBLE
            txt_cart_total.text = getString(R.string.lable_cart_total)
            recycler_scan_cart_lits.visibility = View.VISIBLE
            rele_scan_pay.visibility = View.VISIBLE
            cart_top_total.text = sessionManager.subTotal
            Log.e("===== fetchWeather()", "${db.todoDao().getAll()}")
            detailsAdapter = ScanProductDetailsAdapter(
                this, productsData.reversed(), cart_top_total,
                txt_cart_count, recycler_scan_cart_lits, rele_scan_pay, title
            )
            recycler_scan_cart_lits.adapter = detailsAdapter
        } else {
            sessionManager.cartId = "0"
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 0)
            cart_include.layoutParams = params
            (scan_layout.layoutParams as RelativeLayout.LayoutParams).height =
                RelativeLayout.LayoutParams.MATCH_PARENT
            scan_preview.visibility = View.VISIBLE
            scan_preview_small.visibility = View.GONE
            txt_cart_count.visibility = View.INVISIBLE
            txt_cart_total.text = getString(R.string.er_cart_empty1)
            cart_top_total.text = getString(R.string.er_cart_empty)
            recycler_scan_cart_lits.visibility = View.GONE
            rele_scan_pay.visibility = View.GONE

        }
        //}
        /* }
         mDbWorkerThread?.postTask(task)*/
    }


    private fun insertWeatherDataInDb(productsData: ArrayList<ProductByCodeModel>) {
        // val task = Runnable {
        productsData.forEach {
            if (it.quantity == 0) {
                it.quantity = 1
            }
            db.todoDao().insertAll(
                ProductByCodeModel(
                    it.quantity,
                    0,
                    it.productTotal,
                    0,
                    it.productId,
                    it.storeId,
                    it.pluCode,
                    it.r_name,
                    it.salesPrice,
                    it.manual_price,
                    it.offer,
                    it.pluBarcode
                )
            )
            AddUpdateShowDialog(1)//add Dailotg
        }
        // }
        //  mDbWorkerThread?.postTask(task)
    }

    private fun insertshowDetails(barcodeResult: String) {

        if (db.todoDao().getAll().isNotEmpty()) {
            for (i in db.todoDao().getAll().indices) {
                if (db.todoDao().getAll()[i].pluBarcode) {
                    if (db.todoDao().getAll()[i].pluCode == barcodeResult) {
                        //Toast.makeText(this,"Product already scanned.",Toast.LENGTH_LONG).show()
                        /* itemId = db.todoDao().getAll()[i].productId
                         quantity = db.todoDao().getAll()[i].quantity
                         position = i*/
                        // validBC = "1"

                        dialogstatus = true
                        break

                    }
                } else {

                    if (db.todoDao().getAll()[i].pluCode == barcodeResult) {
                        pluCode = db.todoDao().getAll()[i].pluCode
                        quantity = db.todoDao().getAll()[i].quantity
                        position = i
                        validBC = "1"
                    } else {

                    }

                }

            }

            if (validBC == "1") {
//                showAlertDialoge(itemId, quantity, position)
                AddUpdateShowDialog(2)//update Dailotg
                addToCartAgain(pluCode, quantity, position);
            } else {
                Utils.showProgressDialog(this, true)

                try {
                    // Extract the numeric part from the URL
                    val numericPart = barcodeResult.replace("[^\\d]".toRegex(), "")
                    Log.d("15nov:","numericPart: "+ numericPart)

                    // Check if the numeric part is not empty before parsing to Long
                    if (numericPart.isNotEmpty()) {
                        val formattedBarcode = String.format(Locale.ENGLISH, "%013d", numericPart.toLong())

                        mPresenter.getProductDetails(
                            sessionManager.shopId.toString(),
                            formattedBarcode,
                            location,
                            sessionManager.userId.toString()
                        )
                    } else {
                        // Handle the case where the numeric part is empty
                        // Add your error handling or notify the user
                    }
                } catch (e: NumberFormatException) {
                    // Handle the exception gracefully, for example, log an error message or notify the user.
                    e.printStackTrace() // Log the exception for debugging purposes
                    // Add your error handling logic here
                }
            }
        } else {


            Utils.showProgressDialog(this, true)
          /*  mPresenter.getProductDetails(
                sessionManager.shopId.toString(),
                String.format(Locale.ENGLISH, "%013d", barcodeResult.toLong()),
                location, sessionManager.userId.toString()
            )*/

            try {
                // Extract the numeric part from the URL
                val numericPart = barcodeResult.replace("[^\\d]".toRegex(), "")
                Log.d("15nov:","numericPart:else : "+ numericPart)

                // Check if the numeric part is not empty before parsing to Long
                if (numericPart.isNotEmpty()) {
                    val formattedBarcode = String.format(Locale.ENGLISH, "%013d", numericPart.toLong())

                    mPresenter.getProductDetails(
                        sessionManager.shopId.toString(),
                        formattedBarcode,
                        location,
                        sessionManager.userId.toString()
                    )
                } else {
                    // Handle the case where the numeric part is empty
                    // Add your error handling or notify the user
                }
            } catch (e: NumberFormatException) {
                // Handle the exception gracefully, for example, log an error message or notify the user.
                e.printStackTrace() // Log the exception for debugging purposes
                // Add your error handling logic here
            }

        }
        if (dialogstatus) {
            dialog()
            dialogstatus = false
        }
    }

    private fun dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.product_is_already_scanned)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                //activity!!.onBackPressed()

            }
        val alert = builder.create()
        alert.show()
    }

    private fun createCameraFocus() {

        barcodeDetector = BarcodeDetector.Builder(this).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(1600, 1024)
            .build()
        //camera_preview.holder.getSurface()
        camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

                if (ActivityCompat.checkSelfPermission(
                        this@BarcodeScannerActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@BarcodeScannerActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        1
                    )
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    // int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                try {

                    cameraSource.start(camera_preview.holder)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }
        })
        scanBarcode()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Please provide camera permission for scanning barcodes",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    if (cameraSource != null) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return
                        }
                        cameraSource.start(camera_preview.holder)
                    }
                }
            }
        }
    }

    private fun scanBarcode() {

//        Log.e("second====", "${timeIndex}")

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                if (timeIndex > 10) {
                    val barcodes = detections.detectedItems
                    if (barcodes.size() > 0) {
                        timeIndex = 0
                        barcodeDetector.release()
                        //stops the scanner from reading multiple values
                        txt_barcode.post(Runnable {
                            barcodeResult = barcodes.valueAt(0).displayValue
                            txt_barcode.text = barcodeResult
                            // sessionManager.setBarcodeResult(barcodeResult);
                            showDetails(barcodeResult)
                            apiresponseStatus = false
                        })
                    }
                }

            }
        })


    }

    private fun showDetails(barcodeResult: String) {
//        barcodeDetector.release()
        //stops the scanner from reading multiple values
        validBC = "0"
        var status = false
        insertshowDetails(barcodeResult.trim())

    }

    private fun showAlertDialoge(itemId: Int, quantity: Int, position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(resources.getString(R.string.txt_alert))
        alertDialogBuilder
            .setMessage(resources.getString(R.string.txt_alert_desc))
            .setCancelable(false)
            .setPositiveButton(
                resources.getString(R.string.txt_yes)
            ) { dialog, id -> addToCartAgain(pluCode, quantity, position) }
            .setNegativeButton(
                resources.getString(R.string.txt_no)
            ) { dialog, id ->
                dialog.cancel()
                scanBarcode()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun addToCartAgain(pluCode: String, quantity: Int, position: Int) {
        db.todoDao().update(quantity + 1, pluCode)
        Log.e("===== addToCartAgain()", "${db.todoDao().getAll()}")
        detailsAdapter?.notifyDatachanged(db.todoDao().getAll().reversed())
        scanBarcode()
        AddUpdateShowDialog(2)//update Dailotg
        //update dailog
    }

    fun AddUpdateShowDialog(i: Int) {
        val dialog = Dialog(this, R.style.MyDialogTheme1)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_quntity_update)
        val dialogtext: TextView = dialog.findViewById(R.id.tv_dsc) as TextView
        val dialogButton: RelativeLayout = dialog.findViewById(R.id.rl_mainview) as RelativeLayout

        Handler().postDelayed({
            dialog.dismiss()
        }, 1000)
        when (i) {

            1 -> {
                dialogtext.setText(getString(R.string.dailog_add_qnty))
            }

            2 -> {
                dialogtext.setText(getString(R.string.dailog_update_qnty))
            }
        }
//        switch (i) {
//            case 1:
//            break
//            case 1:
//            break
//        }
//        if (sessionManager.languageselect == "en") {
//            dialogtext.setText(getString(R.string.dailog_update_qnty))
//        } else {
//            dialogtext.setText(getString(R.string.dailog_update_qnty))
//        }

        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        }
        )
        dialog.show()
    }

    override fun showProductDetails(response: StatusProductByCodeModel) {
        Utils.pd.dismiss()
        mFullDatum.clear()
        if (response.status == 200) {
            response.data?.let { mFullDatum.add(it) }
//            Log.e("response prodetail", "${mFullDatum.size}"+" response.data.tostring= "+response.toString() )
            Log.e(
                "response prodetail 1",
                " response.data?.pluBarcode= " + response.data?.pluBarcode
            )

            if (db.todoDao().getAll().isNotEmpty()) {
                val savedPrev =
                    db.todoDao().getAll().findLast { it.pluCode.equals(response.data?.pluCode) }
                if (savedPrev?.pluCode.equals(response.data?.pluCode)) {

                    if (apiresponseStatus == false) {
                        showDetails(response.data?.pluCode.toString())
                        apiresponseStatus = true
                    }

                } else {//insert
                    insertWeatherDataInDb(productsData = mFullDatum)
                    scanBarcode()
                }
            } else {
                insertWeatherDataInDb(productsData = mFullDatum)
                scanBarcode()
            }
            fetchWeatherDataFromDb()
        }
       /* else if (response.status == 401) {
            finish()
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }

        }*/ else {
            if (sessionManager.languageselect == "en") {
                showMessage(response.statusMessage)
            } else {
                showMessage(response.statusMsgArabic)
            }
            scanBarcode()
        }

    }

    override fun showReloadButton(throwable: Throwable, errorBody: ErrorBody?, network: Boolean) {
        Utils.pd.dismiss()
        when {
            network -> showMessage(getString(R.string.no_internet))
            errorBody != null -> showMessage(errorBody.toString())
            throwable is SocketException -> showMessage(throwable.message!!)
            else -> showMessage(throwable.message!!)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_left_arrow -> {
                onBackPressed()
            }
            R.id.rele_scan_pay -> {
                cartDetailsJsonFormat()
                val intent = Intent(this@BarcodeScannerActivity, CheckoutActivity::class.java)

                intent.putExtra("cartDetails", jsonArray.toString())

                Log.d("Issueeee:",jsonArray.toString())
                startActivity(intent)
            }
            R.id.txt_info -> {
                popupWindow(R.style.DialogAnimation1)
            }
            R.id.txt_heading -> {
                filtercustomDialog!!.dismiss()
            }
            R.id.edt_enter_barcode -> {
                openBarCodeDialog()


            }
        }
    }

    private fun openBarCodeDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_barcode, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.show()
        mDialogView.btnSubmit.setOnClickListener {
            val barcode = mDialogView.etBarCode.text.toString()
            if (TextUtils.isEmpty(barcode)) {
                mDialogView.etBarCode.setError(getString(R.string.txt_enter_barcode))
            } else {
                mAlertDialog.dismiss()
                showDetails(barcode)
                apiresponseStatus = false
            }
        }
        mDialogView.btnCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        filtercustomDialog = null
    }

    private fun popupWindow(animationSource: Int) {
        val inflater = layoutInflater
        val customView = inflater.inflate(R.layout.dialog_info, null)
        // Build the dialog
        filtercustomDialog = Dialog(this, R.style.MyDialogTheme)
        filtercustomDialog!!.setContentView(customView)
        // dialog with bottom and with out padding
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var screenHeight = displayMetrics.heightPixels / 2
        val window = filtercustomDialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT
        wlp.height = screenHeight
        window.attributes = wlp
        filtercustomDialog!!.show()
        filtercustomDialog!!.rvSteps.apply {
            adapter = StepInfoAdapter(this@BarcodeScannerActivity, getStepList())
        }
        filtercustomDialog!!.txt_heading.setOnClickListener(this)
        filtercustomDialog!!.setCancelable(false)
        filtercustomDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun getStepList(): ArrayList<StepInfoModel> {
        var infoList = arrayListOf<StepInfoModel>()
        infoList.add(step1())
        infoList.add(step2())
        infoList.add(step3())
        infoList.add(step4())
        infoList.add(step5())
        infoList.add(step6())
        return infoList
    }

    private fun step2(): StepInfoModel {
        var imageGetter: Html.ImageGetter = Html.ImageGetter {
            val d = ContextCompat.getDrawable(this, R.drawable.ic_home)
            d!!.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
            d
        }
        val spanned1 = Html.fromHtml(
            if (isSelsectedLanguage) "Step 2: Start your <font color='#e62625'><b>Scan & Pay</b></font> journey by opening your Monoprix App " else "الخطوة 2: ابدأ ملف <font color='#e62625'><b>امسح وادفع</b></font> رحلة عن طريق فتح تطبيق مونوبري الخاص بك ",
            null,
            null
        )
        return StepInfoModel(spanned1)
    }

    private fun step3(): StepInfoModel {
        val spanned1 = Html.fromHtml(
            if (isSelsectedLanguage) "Step 3: Select <font color='#e62625'><b>Scan & Pay</b></font> icon " else "الخطوة 3: حدد <font color='#e62625'><b>امسح وادفع</b></font> أيقونة ",
            null,
            null
        )
        return StepInfoModel(spanned1)
    }

    private fun step4(): StepInfoModel {
        var imageGetter: Html.ImageGetter = Html.ImageGetter {
            val d = ContextCompat.getDrawable(this, R.drawable.step_barcod)
            d!!.setBounds(0, 0, iconHeight, iconHeight)
            d
        }
        val spanned1 = Html.fromHtml(
            if (isSelsectedLanguage) "Step 4: Scan your products <img src= /> "
            else "الخطوة 4: فحص منتجاتك <img src= /> ",
            imageGetter,
            null
        )
        return StepInfoModel(spanned1)
    }

    private fun step5(): StepInfoModel {
        var imageGetter1: Html.ImageGetter = Html.ImageGetter {
            val d = ContextCompat.getDrawable(this, R.drawable.step_cart)
            d!!.setBounds(0, 0, iconHeight, iconHeight)
            d
        }
        var imageGetter2: Html.ImageGetter = Html.ImageGetter {
            val d = ContextCompat.getDrawable(this, R.drawable.step_schedule)
            d!!.setBounds(0, 0, iconHeight, iconHeight)
            d
        }
        val spanned1 = TextUtils.concat(
            Html.fromHtml(
                if (isSelsectedLanguage) "Step 5: Select takeaway <img src= /> or delivery option as per your convenience " else " الخطوة 5: اختر الوجبات الجاهزة <img src= /> أو خيار التسليم حسب راحتك  ",
                imageGetter1,
                null
            ), Html.fromHtml(
                "<img src= />",
                imageGetter2,
                null
            )
        )
        return StepInfoModel(spanned1.toSpanned())
    }

    private fun step6(): StepInfoModel {
        var imageGetter1: Html.ImageGetter = Html.ImageGetter {
            val d = ContextCompat.getDrawable(this, R.drawable.step_mobile_payment)
            d!!.setBounds(0, 0, iconHeight, iconHeight)
            d
        }
        var imageGetter2: Html.ImageGetter = Html.ImageGetter {
            val d = ContextCompat.getDrawable(this, R.drawable.step_cash_counter)
            d!!.setBounds(0, 0, iconHeight, iconHeight)
            d
        }
        val spanned1 = TextUtils.concat(
            Html.fromHtml(                                                     /*or at cash counter*/
                if (isSelsectedLanguage) "Step 6: Pay directly in app <img src= />  " else "الخطوة 6: الدفع مباشرة في التطبيق <img src= /> ",
                imageGetter1,
                null
            )/* Html.fromHtml(
                "<img src= />",
                imageGetter2,
                null
            )*/
        )
        return StepInfoModel(spanned1.toSpanned())
    }

    private fun step1(): StepInfoModel {
        val spanned1 = Html.fromHtml(
            if (isSelsectedLanguage) "Step 1: Welcome to Monoprix!" else " الخطوة 1: مرحبًا بكم في ",
            null,
            null
        )
        return StepInfoModel(spanned1)
    }

    private fun cartDetailsJsonFormat() {
        jsonArray = JSONArray()
        var productData: List<ProductByCodeModel> = db.todoDao().getAll()
        for (z in productData.indices) {
            try {
                jsonobject = JSONObject()
                jsonobject.put("ProductId", productData.get(z).productId)
                jsonobject.put("Quantity", productData.get(z).quantity)
                jsonobject.put("StoreId", productData.get(z).storeId)
                jsonobject.put("salesPrice", productData.get(z).salesPrice)
                jsonobject.put("manual_price", productData.get(z).manual_price)
                jsonobject.put("UserId", sessionManager.userId)
                if (sessionManager.cartId == "") {
                    jsonobject.put("cartId", "0")
                } else {
                    jsonobject.put("cartId", sessionManager.cartId)
                }

                jsonArray.put(jsonobject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        println("Size of sendDetails Map: $jsonArray")
    }

    override fun onStart() {
        super.onStart()
        createCameraFocus()
        detailsAdapter?.apply {
            Log.e("===== start()", "${db.todoDao().getAll()}")
            notifyDatachanged(db.todoDao().getAll().reversed())
            sessionManager.totalCart = db.todoDao().getAll().size.toString()
            if (!TextUtils.isEmpty(sessionManager.totalCart) && sessionManager.totalCart != "0") {
                txt_cart_count.text = sessionManager.totalCart
                txt_cart_count.visibility = View.VISIBLE
                txt_cart_total.text = getString(R.string.lable_cart_total)
                cart_top_total.text = sessionManager.subTotal
                rele_scan_pay.visibility = View.VISIBLE
            } else {
                txt_cart_count.text = "0"
                txt_cart_count.visibility = View.INVISIBLE
                txt_cart_total.text = getString(R.string.er_cart_empty1)
                cart_top_total.text = getString(R.string.er_cart_empty)
                rele_scan_pay.visibility = View.GONE
            }


        }

    }

    override fun onStop() {
        super.onStop()
        cameraSource.stop()

    }


}