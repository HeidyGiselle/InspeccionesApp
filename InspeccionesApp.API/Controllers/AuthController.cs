using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace InspeccionesApp.API.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterDto dto)
        {
            var result = await _authService.RegisterAsync(dto);
            if (result == null)
                return BadRequest(new { mensaje = "El correo ya está registrado." });

            return Ok(result);
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginDto dto)
        {
            var result = await _authService.LoginAsync(dto);
            if (result == null)
                return Unauthorized(new { mensaje = "Correo o contraseña incorrectos." });

            return Ok(result);
        }
    }
}