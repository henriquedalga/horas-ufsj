package com.universidade.sighoras.service;

import com.universidade.sighoras.entity.Solicitacao;
import com.universidade.sighoras.template.EmailTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private String now() {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void sendEmail(String to, EmailTemplate tpl, Map<String,String> vars) {
        String subject = tpl.renderSubject(vars);
        String body    = tpl.renderBody(vars);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendSubmissionEmail(Solicitacao sol) {
        Map<String,String> vars = new HashMap<>();
        vars.put("matricula", sol.getMatricula().toString());
        vars.put("nome",      sol.getNome());
        vars.put("horaTipo",  sol.getHoraTipo().name());
        vars.put("data",      now());

        sendEmail(sol.getEmail(), EmailTemplate.SUBMISSION, vars);
    }

    public void sendApprovalEmail(Solicitacao sol) {
        Map<String,String> vars = new HashMap<>();
        vars.put("matricula", sol.getMatricula().toString());
        vars.put("nome",      sol.getNome());
        vars.put("horaTipo",  sol.getHoraTipo().name());
        vars.put("data",      now());

        sendEmail(sol.getEmail(), EmailTemplate.APPROVAL, vars);
    }

    public void sendRejectionEmail(Solicitacao sol, String comentario) {
        Map<String,String> vars = new HashMap<>();
        vars.put("matricula",  sol.getMatricula().toString());
        vars.put("nome",       sol.getNome());
        vars.put("horaTipo",   sol.getHoraTipo().name());
        vars.put("data",       now());
        vars.put("comentario", comentario);

        sendEmail(sol.getEmail(), EmailTemplate.REJECTION, vars);
    }
}
