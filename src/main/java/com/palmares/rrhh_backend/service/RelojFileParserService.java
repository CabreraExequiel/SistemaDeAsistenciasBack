package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.MarcacionArchivoDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RelojFileParserService {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * Formato esperado ya normalizado:
     * No Mchn EnNo Nombre Mode IOMd Fecha Hora
     *
     * Ejemplo:
     * 001551 1 000000449 QUINTANADANTE 1 0 2025/03/05 13:41:35
     */
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(.+?)\\s+(\\d+)\\s+(\\d+)\\s+(\\d{4}/\\d{2}/\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2})$"
    );

    public List<MarcacionArchivoDto> parsear(MultipartFile file) {
        List<MarcacionArchivoDto> resultado = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_16LE))) {

            String linea;

            while ((linea = br.readLine()) != null) {
                if (linea == null || linea.trim().isEmpty()) {
                    continue;
                }

                String normalizada = normalizarLinea(linea);

                System.out.println("LINEA ORIGINAL: [" + linea + "]");
                System.out.println("LINEA NORMALIZADA: [" + normalizada + "]");

                if (normalizada.isBlank()) {
                    continue;
                }

                // Omitir encabezados típicos
                String lower = normalizada.toLowerCase();
                if (lower.contains("enno") || lower.contains("datetime") || lower.contains("no mchn")) {
                    System.out.println("ENCABEZADO DETECTADO, SE OMITE");
                    continue;
                }

                Matcher matcher = LINE_PATTERN.matcher(normalizada);

                if (!matcher.matches()) {
                    System.out.println("LINEA OMITIDA: no coincide con el formato esperado");
                    continue;
                }

                try {
                    String mchn = matcher.group(2).trim();
                    String enNo = matcher.group(3).trim();
                    String legajoNormalizado = normalizarLegajo(enNo);
                    String nombre = matcher.group(4).trim();
                    String mode = matcher.group(5).trim();
                    String ioMd = matcher.group(6).trim();
                    String fecha = matcher.group(7).trim();
                    String hora = matcher.group(8).trim();

                    LocalDateTime fechaHora = LocalDateTime.parse(fecha + " " + hora, DATE_TIME_FORMAT);

                    MarcacionArchivoDto dto = new MarcacionArchivoDto();
                    dto.setLegajo(enNo);
                    dto.setLegajoNormalizado(legajoNormalizado);
                    dto.setMchn(mchn);
                    dto.setMode(mode);
                    dto.setIoMode(ioMd);
                    dto.setNombreEnArchivo(nombre);
                    dto.setFechaHora(fechaHora);

                    resultado.add(dto);

                } catch (DateTimeParseException e) {
                    System.out.println("LINEA OMITIDA: fecha/hora inválida -> [" + normalizada + "]");
                } catch (Exception e) {
                    System.out.println("LINEA OMITIDA: error inesperado -> [" + normalizada + "]");
                }
            }

            System.out.println("TOTAL FILAS PARSEADAS: " + resultado.size());
            return resultado;

        } catch (Exception e) {
            throw new RuntimeException("No se pudo parsear el archivo del reloj: " + e.getMessage(), e);
        }
    }

    private String normalizarLinea(String linea) {
        if (linea == null) {
            return "";
        }

        String s = linea;

        // Quitar caracteres invisibles
        s = s.replace("\u0000", "");
        s = s.replace("\uFEFF", "");

        // Trim inicial
        s = s.trim();

        // Si la línea viene con caracteres espaciados tipo:
        // "0 0 1 5 5 1    1    0 0 0 0 0 0 4 4 9 ..."
        // unimos letras/dígitos separados por un solo espacio
        s = s.replaceAll("(?<=\\p{L}|\\d) (?=\\p{L}|\\d)", "");

        // Normalizar fecha y hora por si vienen separadas con espacios alrededor
        s = s.replaceAll("\\s*/\\s*", "/");
        s = s.replaceAll("\\s*:\\s*", ":");

        // Reducir bloques múltiples a un solo espacio
        s = s.replaceAll("\\s{2,}", " ").trim();

        return s;
    }

    private String normalizarLegajo(String legajo) {
        if (legajo == null) {
            return null;
        }

        String limpio = legajo.trim();

        if (limpio.isEmpty()) {
            return null;
        }

        // sacar ceros a la izquierda
        limpio = limpio.replaceFirst("^0+", "");

        // si eran todos ceros, queda vacío
        if (limpio.isBlank()) {
            return null;
        }

        // si querés exigir solo números, esto ayuda
        if (!limpio.matches("\\d+")) {
            return null;
        }

        return limpio;
    }
}