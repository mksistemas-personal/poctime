package app.mkiniz.poctime.base.document.bra;

import app.mkiniz.poctime.base.CountryConstant;
import app.mkiniz.poctime.base.document.Document;
import lombok.Builder;

import java.util.InputMismatchException;

@Builder
public record CpfDocument(String identifier) implements Document<String, String> {

    private static final String REGEX_CARACTERES_FORMATACAO = "[./-]";

    public CpfDocument {
        identifier = removeCaracteresFormatacao(identifier);
    }

    @Override
    public String getType() {
        return "cpf";
    }

    @Override
    public String getCountry() {
        return CountryConstant.BR;
    }

    @Override
    public String getComplement() {
        return null;
    }

    @Override
    public boolean hasComplement() {
        return false;
    }

    @Override
    public boolean isValid() {
        return isCpf(identifier);
    }

    public boolean isCpf(String value) {
        if (isCommonInvalid(value))
            return (false);
        try {
            char dig10 = calculate(10, 9, value);
            char dig11 = calculate(11, 10, value);
            return (dig10 == value.charAt(9)) && (dig11 == value.charAt(10));
        } catch (InputMismatchException err) {
            return (false);
        }
    }

    private String removeCaracteresFormatacao(String cnpj) {
        return cnpj.trim().replaceAll(REGEX_CARACTERES_FORMATACAO, "");
    }

    private char calculate(int initialWeight, int maxValue, String value) {
        int sm = 0;
        int weight = initialWeight;
        for (int i = 0; i < maxValue; i++) {
            int num = (int) (value.charAt(i) - 48);
            sm = sm + (num * weight);
            weight--;
        }

        int r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            return '0';
        return (char) (r + 48);
    }

    private static boolean isCommonInvalid(String value) {
        return (value.length() != 11) ||
                value.equals("00000000000") || value.equals("11111111111") ||
                value.equals("22222222222") || value.equals("33333333333") ||
                value.equals("44444444444") || value.equals("55555555555") ||
                value.equals("66666666666") || value.equals("77777777777") ||
                value.equals("88888888888") || value.equals("99999999999");
    }

}
