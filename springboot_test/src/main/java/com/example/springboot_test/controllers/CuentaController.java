package com.example.springboot_test.controllers;

import com.example.springboot_test.dtos.TransaccionDTO;
import com.example.springboot_test.models.Cuenta;
import com.example.springboot_test.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// no podemos usar @controller a secas, porque vamos a trabajar con jsons, recibir y enviar json. vamos a trabajar con una api rest full
// esta anotacion incluye @controller y además agrega @ResponseBody, eso quiere decir que cada metodo del controlador devuelve al cliente una respuesta que se convierte autmaticamente en json y ese contenido va dentro del cuerpo de la respuesta, combinación de rest y controller
@RestController
//@Controller
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping // la ruta es por defecto la del requestmappong
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> listar(){
        return cuentaService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return cuentaService.save(cuenta);
    }

    // lo configuramos con anotaciones para que sea un metodo manejador de request, metodo handler del controllador, maneja una solicitud web
   @GetMapping("/{id}")
   @ResponseStatus(HttpStatus.OK)
    public Cuenta detalle(@PathVariable Long id){
        return cuentaService.findById(id);
   }

   @PostMapping("/transferir")
   // ResponseEntity significa un objeto que estamos guardando en el cuerpo del response
    public ResponseEntity<?> transferir(@RequestBody TransaccionDTO dto){ // con @RequestBody indico que ese el json
        cuentaService.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());

       Map<String, Object> response = new HashMap<>();
      // response.put("date", LocalDate.now()); // produce falla de faster xml
       response.put("status","ok");
       response.put("mensaje","Transferencia realizada con éxito");
       response.put("transaccion", dto);
       return ResponseEntity.ok(response);
   }



}
