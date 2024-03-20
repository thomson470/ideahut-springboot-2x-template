package net.ideahut.springboot.template.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportInput;
import net.ideahut.springboot.report.ReportType;
import net.ideahut.springboot.template.object.ReportData;
import net.ideahut.springboot.util.FrameworkUtil;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/*
 * Contoh penggunaan ReportHandler
 */
@ComponentScan
@RestController
@RequestMapping("/report")
class ReportController {
	
	@Autowired
	private ReportHandler reportHandler;
	
	@GetMapping
	protected ResponseEntity<StreamingResponseBody> get(
		@RequestParam("name") String name
	) {
		ReportType type = getType(name);
		StreamingResponseBody body = response -> {
			try {
				InputStream template = ReportController.class.getClassLoader().getResourceAsStream("report/sample.jasper");
				InputStream imageHeader = ReportController.class.getClassLoader().getResourceAsStream("report/tree1.png");
				InputStream imageDetail = ReportController.class.getClassLoader().getResourceAsStream("report/tree2.png");
				JasperReport report = (JasperReport) JRLoader.loadObject(template);
				
				ReportInput input = new ReportInput();
				input.setType(type);
				input.setReport(report);
				input.setParameter("MAIN_TITLE", "Contoh Report");
				input.setParameter("SUB_TITLE", type.name());
				input.setParameter("IMAGE_HEADER", imageHeader);
				input.setParameter("IMAGE_DETAIL", imageDetail);
				
				List<ReportData> datasource = new ArrayList<>();
				for (long i = 0; i < 100; i++) {
					ReportData data = new ReportData();
					data.setDescription("Deskripsi - " + System.nanoTime());
					data.setName("Name - " + System.nanoTime());
					data.setNumber(i);
					data.setUuid(UUID.randomUUID().toString());
					datasource.add(data);
				}
				input.setDatasource(datasource);
				
				reportHandler.exportReport(input, response);
			} catch (Exception e) {
				throw FrameworkUtil.exception(e);
			}
		};
		return ResponseEntity.ok().contentType(MediaType.valueOf(type.getContentType())).body(body);
	}
	
	private static ReportType getType(String name) {
		try {
			return ReportType.valueOf(name.trim().toUpperCase());
		} catch (Exception e) {
			return ReportType.PDF;
		}
	}
	
}
