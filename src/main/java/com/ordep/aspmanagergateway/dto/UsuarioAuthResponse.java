package com.ordep.aspmanagergateway.dto;


import com.ordep.aspmanagergateway.enums.Perfil;
import com.ordep.aspmanagergateway.enums.StatusRegistro;

public record UsuarioAuthResponse(
        Long id,
        String email,
        String senhaCriptografada,
        Perfil perfil,
        StatusRegistro statusRegistro) {
}
