using InspeccionesApp.Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace InspeccionesApp.API.Controllers
{
    [ApiController]
    [Route("api/audios")]
    [Authorize]
    public class AudiosController : ControllerBase
    {
        private readonly IAudioService _service;

        public AudiosController(IAudioService service)
        {
            _service = service;
        }

        [HttpGet("inspeccion/{inspeccionId}")]
        public async Task<IActionResult> GetByInspeccion(int inspeccionId)
        {
            var lista = await _service.GetByInspeccionAsync(inspeccionId);
            return Ok(lista);
        }

        [HttpPost("inspeccion/{inspeccionId}")]
        public async Task<IActionResult> Upload(int inspeccionId, IFormFile archivo)
        {
            if (archivo == null || archivo.Length == 0)
                return BadRequest(new { mensaje = "No se envió ningún archivo." });

            var subida = await _service.UploadAsync(inspeccionId, archivo);
            return Ok(subida);
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