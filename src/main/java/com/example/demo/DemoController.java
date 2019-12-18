package com.example.demo;

import com.itextpdf.html2pdf.HtmlConverter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.jsoup.Jsoup;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    //@GetMapping("/download")
    @RequestMapping(value = "/download",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<InputStreamResource> get() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=relatorio.pdf");

        ByteArrayInputStream bytes = convertHtmlToPdf();

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bytes));
    }

    private ByteArrayInputStream generatePdfToStream() {

        try {

            JasperReport jasperReport = loadPathTemplate("/reports/relatorio_teste.jrxml");
            Map<String, Object> params = new HashMap<>();

            JRDataSource dataSource = loadDataSource();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            ByteArrayOutputStream pdfReportStream = exportPdfToStream(jasperPrint);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfReportStream.toByteArray());

            pdfReportStream.close();

            return byteArrayInputStream;

        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JRDataSource loadDataSource(){
        ArrayList<String> reportItems = new ArrayList<>();
        reportItems.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRJXR-yIRUVpzA0gVSNpbeQDhd6gtA89LCAkMCEESuUELD68hfg");
        reportItems.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRJXR-yIRUVpzA0gVSNpbeQDhd6gtA89LCAkMCEESuUELD68hfg");
        reportItems.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRJXR-yIRUVpzA0gVSNpbeQDhd6gtA89LCAkMCEESuUELD68hfg");
        reportItems.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRJXR-yIRUVpzA0gVSNpbeQDhd6gtA89LCAkMCEESuUELD68hfg");
        reportItems.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRJXR-yIRUVpzA0gVSNpbeQDhd6gtA89LCAkMCEESuUELD68hfg");


        return new JRBeanCollectionDataSource(reportItems);
    }

    private ByteArrayOutputStream exportPdfToStream(JasperPrint jasperPrint) throws JRException {
        JRPdfExporter pdfExporter = new JRPdfExporter();
        pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();

        pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
        pdfExporter.exportReport();

        return pdfReportStream;
    }

    private JasperReport loadPathTemplate(String template) throws JRException {

        final InputStream reportInputStream = getClass().getResourceAsStream(template);
        final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);

        return JasperCompileManager.compileReport(jasperDesign);
    }

    private ByteArrayInputStream convertHtmlToPdf(){
        try {

            ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
            String htmlStr = extractText(getClass().getResource("/templates/index.html").getPath());
            HtmlConverter.convertToPdf(htmlStr, pdfReportStream);

            return new ByteArrayInputStream(pdfReportStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String extractText(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ( (line=br.readLine()) != null) {
            sb.append(line);
        }
        String textOnly = Jsoup.parse(sb.toString()).text();
        return textOnly;
    }
}