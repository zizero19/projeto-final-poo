package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatacaoUtil {

    private FormatacaoUtil() {
    }

    public static String formatarMoeda(BigDecimal valor) {
        BigDecimal valorSeguro = valor == null ? BigDecimal.ZERO : valor;
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        formatador.setMinimumFractionDigits(2);
        formatador.setMaximumFractionDigits(2);
        return formatador.format(valorSeguro.setScale(2, RoundingMode.HALF_UP));
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "-";
        }
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
