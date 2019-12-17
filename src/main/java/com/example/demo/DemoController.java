package com.example.demo;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;

@RestController
@RequestMapping("/api")
public class DemoController {

    //@GetMapping("/download")
    @RequestMapping(value = "/download", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> get() {
        ByteArrayInputStream bytes = gerarPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=relatorio.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bytes));
    }

    public ByteArrayInputStream gerarPdf() {

        try {

            String file = "/reports/relatorio_teste.jasper";
            //String file = "/reports/relatorio_teste.jrxml";
            InputStream employeeReportStream = getClass().getResourceAsStream(file);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(employeeReportStream);
            //JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null);
            //JRSaver.saveObject(jasperReport, "relatorio_teste.jasper");

            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
            pdfExporter.exportReport();

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

}