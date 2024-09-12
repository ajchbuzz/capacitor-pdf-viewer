package jccham.capacitor.pdf_viewer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.databinding.ActivityPdfViewerBinding;

class PdfViewerFragment : Fragment() {

    private lateinit var pdfUrl: String
    private var top: Int = 0
    private var _binding: ActivityPdfViewerBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(pdfUrl: String, top: Int): PdfViewerFragment {
            val fragment = PdfViewerFragment()
            val args = Bundle()

            args.putString("pdfUrl", pdfUrl)
            args.putInt("top", top)

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState);

        _binding = ActivityPdfViewerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState);

        Log.d("PDFViewerFragment", "Showing progress indicator")

        arguments?.let {
            pdfUrl = it.getString("pdfUrl").orEmpty()
            top = it.getInt("top", 0)
        }

        Log.d("PDFViewerFragment", "PDF $pdfUrl");
        Log.d("PDFViewerFragment", "TOP $top");

        binding.progressBar.visibility = View.VISIBLE;
        binding.pdfView.visibility = View.GONE;

        binding.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadSuccess(absolutePath: String) {
                Log.i("statusCallBack","onPdfLoadSuccess")

                binding.progressBar.visibility  = View.GONE;
                binding.pdfView.visibility = View.VISIBLE;
            }
        }

        // Inicializar la vista PDF con la URL usando el método initWithUrl
        binding.pdfView.initWithUrl(
            url = pdfUrl,
            lifecycleCoroutineScope = lifecycleScope, // Pasamos el LifecycleCoroutineScope correcto
            lifecycle = lifecycle // Pasamos el ciclo de vida del fragmento
        )

        val params = binding.parentLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = top

        binding.parentLayout.layoutParams = params
        val pdfRendererView = binding.pdfView

        binding.myToolbar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
