package ch.nadlo.oss.capacitor.pdf_viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfViewerFragment extends Fragment {

    private String url;
    private Integer top;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    // Método para crear una nueva instancia del fragmento con parámetros
    public static PdfViewerFragment newInstance(String url, int top) {
        PdfViewerFragment fragment = new PdfViewerFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("top", top);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.pdf_rendererview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString("url");
            top = getArguments().getInt("top");
        }

        // Configurar PDFView
        PDFView pdfView = view.findViewById(R.id.pdfView);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        // Descargar y cargar el PDF en segundo plano
        executorService.execute(() -> {
            InputStream inputStream = getPdfInputStream(url);
            if (inputStream != null) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);

                    pdfView.fromStream(inputStream).load();
                });
            }
        });

        // Aplicar el margen superior al layout completo del fragmento
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.pdfLayout); // El LinearLayout del fragmento
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();

        params.topMargin = top; // Aplicar el margen superior dinámico
        layout.setLayoutParams(params); // Actualizar el layout con los nuevos parámetros
    }

    // Método para obtener InputStream desde una URL
    private InputStream getPdfInputStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

