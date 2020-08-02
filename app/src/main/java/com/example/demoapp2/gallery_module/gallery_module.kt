package com.example.demoapp2.gallery_module

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.demoapp2.MainActivity
import com.example.demoapp2.R
import com.example.demoapp2.data.GridAdapter
import com.example.demoapp2.data.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_gallery_module.*
import kotlinx.android.synthetic.main.fragment_gallery_module.view.*
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.Inflater


class gallery_module : Fragment() {

    private var selectedPhotoUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private var fragmentLayout: View? = null
    private var currentUser: FirebaseUser? = null
    //

    private var galleryAdapter: GalleryModuleAdapter? = null
    private var imageArray: MutableList<Bitmap>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //инициализация авторизации
        auth = FirebaseAuth.getInstance()

        //загрузка картинок из облака
        imageArray = mutableListOf()
        GalleryFromCloud()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //макет
        fragmentLayout = inflater.inflate(R.layout.fragment_gallery_module, container, false)

        //получаем авторизованного или нет юзера
        currentUser = auth.currentUser

        //els
        galleryAdapter = GalleryModuleAdapter( getActivity()!!.applicationContext, R.layout.module_grid, imageArray!! )

        fragmentLayout!!.findViewById<GridView>(R.id.grid_moduleGallery).adapter = galleryAdapter

        fragmentLayout!!.findViewById<GridView>(R.id.grid_moduleGallery).onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, selected ->
                Toast.makeText(activity?.baseContext, "Картинка  $position", Toast.LENGTH_SHORT ).show()

                val intent = Intent(this.activity, detailImage::class.java)
                //перед передачей сжать биточки передавать можно интеном только 1м
                val stream = ByteArrayOutputStream()
                imageArray!![position].compress(Bitmap.CompressFormat.JPEG, 100 ,stream)
                val byteArray = stream.toByteArray()
                //
                intent.putExtra(detailImage.IINTENT_BYTEARRAY, byteArray)
                startActivity(intent)
            }
        //

        fragmentLayout!!.findViewById<ProgressBar>(R.id.pb_imgLoad).visibility = View.GONE
        fragmentLayout!!.findViewById<Button>(R.id.btn_upload_image).setOnClickListener{
            Log.d(TAG, "BTN upload clicked")

            /* вызов окна с фотками, чтобы в эмулятор загрузить фото View > Tool Windows > Device File Explorer
            sdcard/ или data/data/app_name/ для приложения */

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 0)  //getActivity() не надо

        }

        return fragmentLayout
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(gallery_module.TAG, "requestCode  $requestCode ${Activity.RESULT_OK} $data")
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            Log.d(gallery_module.TAG, "Фото выбрано $data")

        }
    }*/


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult(requestCode = $requestCode, resultCode = $resultCode)")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 ->{
                    Log.d(gallery_module.TAG, "Фото выбрано $data")
                    //var uri = data?.data
                    //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    //val bitmap = ImageDecoder.createSource(ContentResolver, uri)

//                    if (android.os.Build.VERSION.SDK_INT >= 29){
//                        val bitmap =  ImageDecoder.decodeBitmap(Imagedecoder.createSource())
//                    } else{
//                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                    }
                    //////////////////////////
//                    val bitmap = (iv_gallery.getDrawable() as BitmapDrawable).getBitmap()
//                    val stream = ByteArrayOutputStream()
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
//                    val image = stream.toByteArray()

                    fragmentLayout!!.findViewById<ProgressBar>(R.id.pb_imgLoad).visibility = View.VISIBLE
                    selectedPhotoUri = data!!.data

                    uploadImageToDB()

                    /* Это чтобы поставить в ImageView */
                    try {
                        selectedPhotoUri?.let {
                            if(Build.VERSION.SDK_INT < 28) {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    activity?.getContentResolver(),
                                    selectedPhotoUri
                                )

                                imageArray!!.add(bitmap)
                                galleryAdapter!!.notifyDataSetChanged()
                                //iv_gallery.setImageBitmap(bitmap)
                            } else {
                                val source = ImageDecoder.createSource(getActivity()?.getContentResolver()!!,
                                    selectedPhotoUri!!
                                )
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                imageArray!!.add(bitmap)
                                galleryAdapter!!.notifyDataSetChanged()
                                //iv_gallery.setImageBitmap(bitmap)
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                else ->
                    Log.e(TAG, "onActivityResult: wrong request code!")
            }
        }
    }

    private fun uploadImageToDB() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        Log.d(TAG, "Upload image uri $selectedPhotoUri name $filename")
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Success load image ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    //it.toString()
                    Log.d(TAG, "File loc $it")
                    fragmentLayout!!.findViewById<ProgressBar>(R.id.pb_imgLoad).visibility = View.GONE
                    Toast.makeText(activity?.baseContext,"Картинка успешно загружена", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun GalleryFromCloud(){
        val ref = FirebaseStorage.getInstance().getReference("images")
        ref.listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                    //Log.d(TAG, "prefix of Gallery $prefix")
                }

                listResult.items.forEach { item ->
                    //item.path
                    Log.d(TAG, "item of Gallery ${item.path}")
                    includesForDownloadFiles(item.path)

                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }

    }


    fun includesForDownloadFiles(path : String) {

        val storageRef = FirebaseStorage.getInstance().reference

        //val ONE_MEGABYTE = (1024 * 1024 * 5).toLong()
        storageRef.child(path).getBytes(Long.MAX_VALUE).addOnSuccessListener {bytes->
            // приходит значение в байтах надо преобразовать их
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageArray!!.add(bmp)
            galleryAdapter!!.notifyDataSetChanged()

        }.addOnFailureListener {
            // Handle any errors
        }
    }
    companion object{
        val TAG = MainActivity.TAG
    }


}