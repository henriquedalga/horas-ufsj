package com.universidade.sighoras.template;

import java.util.Map;

public class EmailTemplate {

    public static final EmailTemplate SUBMISSION = new EmailTemplate(
      "Confirmação de Submissão de Horas – Matrícula {{matricula}}",
      "Prezado(a) {{nome}},\n\n" +
      "Sua solicitação de {{horaTipo}} (código {{matricula}}) foi corretamente submetida em {{data}}.\n" +
      "Em breve, nosso setor analisará sua documentação e retornaremos com o resultado.\n\n" +
      "Atenciosamente,\n" +
      "Coordenação de Horas Complementares\n" +
      "Universidade X"
    );

    public static final EmailTemplate APPROVAL = new EmailTemplate(
      "Solicitação Aprovada – Matrícula {{matricula}}",
      "Olá {{nome}},\n\n" +
      "Sua solicitação de {{horaTipo}} (matrícula {{matricula}}) foi **aprovada** em {{data}}.\n" +
      "Parabéns! As horas aparecerão no seu histórico acadêmico em até 48 horas.\n\n" +
      "Saudações,\n" +
      "Coordenação de Horas Complementares"
    );

    public static final EmailTemplate REJECTION = new EmailTemplate(
      "Solicitação Não Aprovada – Matrícula {{matricula}}",
      "Olá {{nome}},\n\n" +
      "Lamentamos, mas sua solicitação de {{horaTipo}} (matrícula {{matricula}}) foi **rejeitada** em {{data}}.\n" +
      "Motivo: {{motivo}}\n\n" +
      "Se precisar de mais informações, entre em contato conosco.\n\n" +
      "Atenciosamente,\n" +
      "Coordenação de Horas Complementares"
    );

    private final String subjectTemplate;
    private final String bodyTemplate;

    private EmailTemplate(String subjectTemplate, String bodyTemplate) {
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate    = bodyTemplate;
    }
        public String renderSubject(Map<String,String> vars) {
        String s = subjectTemplate;
        for (var k : vars.keySet()) {
            s = s.replace("{{" + k + "}}", vars.get(k));
        }
        return s;
    }

    public String renderBody(Map<String,String> vars) {
        String b = bodyTemplate;
        for (var k : vars.keySet()) {
            b = b.replace("{{" + k + "}}", vars.get(k));
        }
        return b;
    }
}

