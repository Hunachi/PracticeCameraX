package io.github.hunachi.practicecamerax

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.LensFacing
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import kotlinx.android.synthetic.main.camera_fragment.*
import java.util.concurrent.Executors


class CameraFragment : Fragment() {

    companion object {
        fun newInstance() = CameraFragment()
    }

    private lateinit var viewModel: CameraViewModel
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.camera_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            (activity as? MainActivity)?.moveToPermission()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .setTargetName("Preview")
            .build()

        preview.previewSurfaceProvider = previewView.previewSurfaceProvider

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(LensFacing.BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.BackpressureStrategy.KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            ObjectDetectionAnalyzer {
                overlay?.post { updateOverlay(it) }
            }
        )

        cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }

    private fun updateOverlay(detectedObjects: DetectedObjects) {
        if (detectedObjects.objects.isEmpty()) {
            overlay.set(emptyList())
            return
        }

        overlay.setSize(detectedObjects.imageWidth, detectedObjects.imageHeight)

        val list = mutableListOf<BoxData>()

        for (obj in detectedObjects.objects) {

            val box = obj.boundingBox

            val name = "${categoryNames[obj.classificationCategory]}"

            val confidence =
                if (obj.classificationCategory != FirebaseVisionObject.CATEGORY_UNKNOWN) {
                    val confidence = obj.classificationConfidence!!.times(100).toInt()
                    "$confidence%"
                } else ""

            list.add(BoxData("$name$confidence", box))
        }

        overlay.set(list)
    }
}
