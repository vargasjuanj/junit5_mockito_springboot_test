package com.example.springboot_test;

import com.example.springboot_test.exceptions.DineroSuficienteExcepcion;
import com.example.springboot_test.models.Banco;
import com.example.springboot_test.models.Cuenta;
import com.example.springboot_test.repositories.BancoRepository;
import com.example.springboot_test.repositories.CuentaRepository;
import com.example.springboot_test.services.CuentaService;
import com.example.springboot_test.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringbootTestApplicationTests {
/*
En la dependencia spring boot test viene incluid junit, mockito y otros
 */
	//@Mock
	@MockBean // podemos usar este en spring
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;

	/*
	Cuando usamos inyección de dependencias con injectMock, es necesario
	usar la implementación del servicio, y no la interfaz. Por que de la forma estatica creabamos el objeto con el new y le inyectabamos por construcción
	Pero en este caso como la Implementación tiene constructor  va a verificar que estos dos mocks son de este tipo de interfaz

	 */
	//@InjectMocks
			// en vez de injectmocks tenemos que hacer que esta clase sea un componente, registrarlo como un bean en el contenedor de spring
	// lo registramos en el contendeor con @Service, ahora inyectamos la dependencia con autowired
	@Autowired // de forma automática se le inyectan los mocks beans de arriba
	/* spring me permite definir el tipo generico, podemos quitar la impl, y dejar la interfaz, no la clase concreta
		Eso es gracias a que spring maneja o permite detectar, en el fondo sería buscar que clase o componente implementa la interfaz y lo va a inyectar
		Va a buscar componente a componente, encuentra y detecta que CuentaService es implementado por CuentaServiceImpl y lo va a inyectar de forma automática
	 */
			CuentaService service;

	@BeforeEach
	void setup(){
		// Esta es la forma de crear  mocks de forma estatica. Pero hay dos formas más una con anotaciones de mockito y otra con anotaciones de spring (Mockbean)
/*		cuentaRepository = mock(CuentaRepository.class);
		bancoRepository = mock(BancoRepository.class);
		service = new CuentaServiceImpl(cuentaRepository, bancoRepository);*/
	}


	@Test
	void contextLoads() {
		//thenReturn() siempre devuelve el mismo valor u objeto en este caso ( es decir se mantiene la referencia y en efecto sus cambios), mientras que thenAnswer() devuelve el valor calculado en tiempo real, como devolver la hora actual.
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(new Datos().cuenta2NoEstatica());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoCuentaOrigen = service.revisarSaldo(1L);
		BigDecimal saldoCuentaDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoCuentaOrigen.toPlainString());
		assertEquals("2000", saldoCuentaDestino.toPlainString());

		service.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoCuentaOrigen = service.revisarSaldo(1L);
		saldoCuentaDestino = service.revisarSaldo(2L);

		assertEquals("900", saldoCuentaOrigen.toPlainString());
		assertEquals("2100", saldoCuentaDestino.toPlainString());

		/* asi van a falalr porque se ejecutan más de una vez, sino que 3 veces
        verify(cuentaRepository).findById(1l));
		verify(cuentaRepository).findById(2l));

		 */

		int totalDeTransferencias = service.revisarTotalTransferencias(1l);
		assertEquals(1, totalDeTransferencias);
		verify(cuentaRepository, times(3)).findById(1l);
		verify(cuentaRepository, times(3)).findById(2l);
		verify(cuentaRepository, times(2)).update(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1l);
		verify(bancoRepository).update(any(Banco.class));
		// 6 veces e sla sumatoria de las veces anterior que se invoca, pero con un long gral
		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(new Datos().cuenta2NoEstatica());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoCuentaOrigen = service.revisarSaldo(1L);
		BigDecimal saldoCuentaDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoCuentaOrigen.toPlainString());
		assertEquals("2000", saldoCuentaDestino.toPlainString());

		assertThrows(DineroSuficienteExcepcion.class, () ->{
			service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});

		saldoCuentaOrigen = service.revisarSaldo(1L);
		saldoCuentaDestino = service.revisarSaldo(2L);

		// se mantiene igual porque al lanzarse la excepción de más arriba no alcanza a modificarse
		assertEquals("1000", saldoCuentaOrigen.toPlainString());
		assertEquals("2000", saldoCuentaDestino.toPlainString());

		int totalDeTransferencias = service.revisarTotalTransferencias(1l);

		// estos valores también cambian producto de la excepción que parte del metodo transferir, para abajo, los times, y esperado
		assertEquals(0, totalDeTransferencias);
		verify(cuentaRepository, times(3)).findById(1l);
		verify(cuentaRepository, times(2)).findById(2l);
		verify(cuentaRepository, never()).update(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1l);
		verify(bancoRepository, never()).update(any(Banco.class));
		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void contextLoad3() {
		when(cuentaRepository.findById(1l)).thenReturn(Datos.crearCuenta001());

		Cuenta cuenta1= service.findById(1l);
		Cuenta cuenta2 = service.findById(1l);
		// vamos a afirmar que la instancia sea la misma
		// son dos formas de comprobar lo mismo
		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		verify(cuentaRepository, times(2)).findById(1l);

	}
}
