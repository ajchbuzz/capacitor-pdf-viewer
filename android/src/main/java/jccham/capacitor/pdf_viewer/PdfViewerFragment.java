package jccham.capacitor.pdf_viewer;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.view.ViewGroup.MarginLayoutParams;

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

    private String pdfUrl;
    private int topMargin;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    // Método para crear una nueva instancia del fragmento con parámetros
    public static PdfViewerFragment newInstance(String pdfUrl, int topMargin) {
        PdfViewerFragment fragment = new PdfViewerFragment();
        Bundle args = new Bundle();
        args.putString("pdfUrl", pdfUrl);
        args.putInt("topMargin", topMargin);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            pdfUrl = getArguments().getString("pdfUrl");
            topMargin = getArguments().getInt("topMargin", 0); // Valor predeterminado 0 si no se pasa
        }

        // Crear el FrameLayout principal
        FrameLayout rootLayout = new FrameLayout(requireContext());
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Crear la barra de progreso centrada verticalmente
        ProgressBar progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleLarge);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        progressParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progressParams);
        rootLayout.addView(progressBar);

        // Crear el PDFView usando la nueva librería
        PDFView pdfView = new PDFView(requireContext(), null);
        pdfView.setVisibility(View.GONE); // Inicialmente oculto
        rootLayout.addView(pdfView);

        // Descargar y cargar el PDF en segundo plano
        executorService.execute(() -> {
            InputStream inputStream = getPdfInputStream(pdfUrl);
            if (inputStream != null) {
                requireActivity().runOnUiThread(() -> {
                    // Ocultar la barra de progreso y mostrar el PDF
                    progressBar.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);

                    // Cargar el PDF directamente desde el InputStream
                    pdfView.fromStream(inputStream).load();
                });
            }
        });

        // Aplicar el margen superior dinámico al layout completo
        MarginLayoutParams params = (MarginLayoutParams) rootLayout.getLayoutParams();
        params.topMargin = topMargin; // Aplicar el margen superior dinámico
        rootLayout.setLayoutParams(params); // Actualizar el layout con los nuevos parámetros

        return rootLayout;
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
