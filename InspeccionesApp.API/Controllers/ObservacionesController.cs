using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InspeccionesApp.API.Controllers
{
    [ApiController]
    [Route("api/observaciones")]
    [Authorize]
    public class ObservacionesController : ControllerBase
    {
        private readonly IObservacionService _service;

        public ObservacionesController(IObservacionService service)
        {
            _service = service;
        }

        [HttpGet("inspeccion/{inspeccionId}")]
        public async Task<IActionResult> GetByInspeccion(int inspeccionId)
        {
            var lista = await _service.GetByInspeccionAsync(inspeccionId);
            return Ok(lista);
        }

        [HttpPost]
        public async Task<IActionResult> Create(ObservacionCreateDto dto)
        {
            var creada = await _service.CreateAsync(dto);
            return Ok(creada);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] string comentario)
        {
            var ok = await _service.UpdateAsync(id, comentario);
            if (!ok) return NotFound();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var ok = await _service.DeleteAsync(id);
            if (!ok) return NotFound();
            return NoContent();
        }
    }
}