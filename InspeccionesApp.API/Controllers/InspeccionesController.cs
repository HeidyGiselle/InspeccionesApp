using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace InspeccionesApp.API.Controllers
{
    [ApiController]
    [Route("api/inspecciones")]
    [Authorize]
    public class InspeccionesController : ControllerBase
    {
        private readonly IInspeccionService _service;

        public InspeccionesController(IInspeccionService service)
        {
            _service = service;
        }

        private int UsuarioId => int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        private string Rol => User.FindFirstValue(ClaimTypes.Role)!;

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var lista = await _service.GetAllAsync(UsuarioId, Rol);
            return Ok(lista);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var inspeccion = await _service.GetByIdAsync(id, UsuarioId, Rol);
            if (inspeccion == null) return NotFound();
            return Ok(inspeccion);
        }

        [HttpPost]
        public async Task<IActionResult> Create(InspeccionCreateDto dto)
        {
            var creada = await _service.CreateAsync(dto, UsuarioId);
            return CreatedAtAction(nameof(GetById), new { id = creada.Id }, creada);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, InspeccionUpdateDto dto)
        {
            var ok = await _service.UpdateAsync(id, dto, UsuarioId, Rol);
            if (!ok) return NotFound();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var ok = await _service.DeleteAsync(id, UsuarioId, Rol);
            if (!ok) return NotFound();
            return NoContent();
        }
    }
}