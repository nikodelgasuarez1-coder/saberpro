package com.saberpro.service;

import com.saberpro.entity.Estudiante;
import com.saberpro.entity.Facultad;
import com.saberpro.entity.Resultado;
import com.saberpro.repository.EstudianteRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Importa y exporta el Excel del ICFES con los resultados Saber Pro.
 * El formato de columnas corresponde al archivo "INFORME SABER PRO".
 */
@Service
public class ExcelService {

    private final EstudianteRepository estudianteRepository;

    public ExcelService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    // Índices de columnas del Excel del ICFES
    private static final int COL_TIPO_DOC = 0;
    private static final int COL_NUM_DOC = 1;
    private static final int COL_APE1 = 2;
    private static final int COL_APE2 = 3;
    private static final int COL_NOM1 = 4;
    private static final int COL_NOM2 = 5;
    private static final int COL_CORREO = 6;
    private static final int COL_TEL = 7;
    private static final int COL_REGISTRO = 8;
    private static final int COL_PUNTAJE = 9;
    private static final int COL_PUNTAJE_NIVEL = 10;
    private static final int COL_COM_ESCRITA = 11;
    private static final int COL_RAZ_CUANT = 13;
    private static final int COL_LEC_CRIT = 15;
    private static final int COL_COMP_CIUD = 17;
    private static final int COL_INGLES = 19;
    private static final int COL_FORM_PROY = 21;
    private static final int COL_PENS_CIENT = 23;
    private static final int COL_DIS_SOFT = 25;
    private static final int COL_NIVEL_INGLES = 27;

    /**
     * Importa el archivo. Cada estudiante se identifica por su Número de registro.
     * Si ya existe se actualiza; si no, se crea. Todos quedan asignados a la
     * facultad indicada (el Excel del ICFES no trae facultad).
     *
     * @return cantidad de estudiantes importados/actualizados.
     */
    public int importar(MultipartFile archivo, Facultad facultad) throws Exception {
        int procesados = 0;

        try (InputStream is = archivo.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // encabezado

                String registro = leerTexto(row, COL_REGISTRO);
                String apellido1 = leerTexto(row, COL_APE1);
                // Filas sin registro ni apellido (ej. fila de promedios) se ignoran
                if ((registro == null || registro.isBlank())
                        && (apellido1 == null || apellido1.isBlank())) {
                    continue;
                }

                Estudiante est = (registro != null)
                        ? estudianteRepository.findByNumeroRegistro(registro).orElse(new Estudiante())
                        : new Estudiante();

                est.setTipoDocumento(valorODefecto(leerTexto(row, COL_TIPO_DOC), "CC"));
                est.setNumeroDocumento(leerTexto(row, COL_NUM_DOC));
                est.setPrimerApellido(apellido1);
                est.setSegundoApellido(leerTexto(row, COL_APE2));
                est.setPrimerNombre(leerTexto(row, COL_NOM1));
                est.setSegundoNombre(leerTexto(row, COL_NOM2));
                est.setCorreo(leerTexto(row, COL_CORREO));
                est.setTelefono(leerTexto(row, COL_TEL));
                est.setNumeroRegistro(registro);
                if (est.getFacultad() == null) {
                    est.setFacultad(facultad);
                }

                // Resultado
                Resultado res = est.getResultado() != null ? est.getResultado() : new Resultado();
                res.setEstudiante(est);

                String celdaPuntaje = leerTexto(row, COL_PUNTAJE);
                boolean anulado = celdaPuntaje != null && celdaPuntaje.toUpperCase().contains("ANULAD");
                res.setAnulado(anulado);

                if (!anulado) {
                    res.setPuntajeGlobal(leerEntero(row, COL_PUNTAJE));
                    res.setNivelGlobal(leerTexto(row, COL_PUNTAJE_NIVEL));

                    res.setComunicacionEscrita(leerEntero(row, COL_COM_ESCRITA));
                    res.setComunicacionEscritaNivel(leerTexto(row, COL_COM_ESCRITA + 1));
                    res.setRazonamientoCuantitativo(leerEntero(row, COL_RAZ_CUANT));
                    res.setRazonamientoCuantitativoNivel(leerTexto(row, COL_RAZ_CUANT + 1));
                    res.setLecturaCritica(leerEntero(row, COL_LEC_CRIT));
                    res.setLecturaCriticaNivel(leerTexto(row, COL_LEC_CRIT + 1));
                    res.setCompetenciasCiudadanas(leerEntero(row, COL_COMP_CIUD));
                    res.setCompetenciasCiudadanasNivel(leerTexto(row, COL_COMP_CIUD + 1));
                    res.setIngles(leerEntero(row, COL_INGLES));
                    res.setInglesNivel(leerTexto(row, COL_INGLES + 1));
                    res.setFormulacionProyectos(leerEntero(row, COL_FORM_PROY));
                    res.setFormulacionProyectosNivel(leerTexto(row, COL_FORM_PROY + 1));
                    res.setPensamientoCientifico(leerEntero(row, COL_PENS_CIENT));
                    res.setPensamientoCientificoNivel(leerTexto(row, COL_PENS_CIENT + 1));
                    res.setDisenoSoftware(leerEntero(row, COL_DIS_SOFT));
                    res.setDisenoSoftwareNivel(leerTexto(row, COL_DIS_SOFT + 1));
                    res.setNivelIngles(leerTexto(row, COL_NIVEL_INGLES));
                }

                est.setResultado(res);
                estudianteRepository.save(est);
                procesados++;
            }
        }
        return procesados;
    }

    /** Genera un Excel (.xlsx) con el listado de estudiantes y sus resultados. */
    public byte[] exportarInforme(List<Estudiante> estudiantes) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Resultados Saber Pro");

            CellStyle header = wb.createCellStyle();
            Font bold = wb.createFont();
            bold.setBold(true);
            header.setFont(bold);

            String[] cols = {"Registro", "Documento", "Estudiante", "Facultad",
                    "Puntaje Global", "Nivel", "Estado", "Aprobado"};
            Row h = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(header);
            }

            int r = 1;
            for (Estudiante e : estudiantes) {
                Resultado res = e.getResultado();
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(nvl(e.getNumeroRegistro()));
                row.createCell(1).setCellValue(nvl(e.getNumeroDocumento()));
                row.createCell(2).setCellValue(nvl(e.getNombreCompleto()));
                row.createCell(3).setCellValue(e.getFacultad() != null ? e.getFacultad().getNombre() : "");
                if (res != null && !res.isAnulado() && res.getPuntajeGlobal() != null) {
                    row.createCell(4).setCellValue(res.getPuntajeGlobal());
                    row.createCell(5).setCellValue(nvl(res.getNivelGlobal()));
                    row.createCell(6).setCellValue(res.getEstado());
                    row.createCell(7).setCellValue(res.isAprobado() ? "SÍ" : "NO");
                } else {
                    row.createCell(6).setCellValue(res != null && res.isAnulado() ? "ANULADO" : "SIN RESULTADO");
                }
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // ---------- helpers ----------
    private String leerTexto(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        String v;
        switch (cell.getCellType()) {
            case STRING  -> v = cell.getStringCellValue();
            case NUMERIC -> v = String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> v = String.valueOf(cell.getBooleanCellValue());
            default      -> v = null;
        }
        if (v != null) {
            v = v.trim();
            if (v.isEmpty()) return null;
        }
        return v;
    }

    private Integer leerEntero(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) Math.round(cell.getNumericCellValue());
            }
            String s = cell.getStringCellValue().trim();
            return s.isEmpty() ? null : (int) Math.round(Double.parseDouble(s));
        } catch (Exception ex) {
            return null;
        }
    }

    private String valorODefecto(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }

    private String nvl(String v) {
        return v == null ? "" : v;
    }
}
