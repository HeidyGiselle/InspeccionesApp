using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using InspeccionesApp.Domain.Entities;
using InspeccionesApp.Infrastructure.Data;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;

namespace InspeccionesApp.Infrastructure.Services
{
    public class AudioService : IAudioService
    {
        private readonly AppDbContext _context;
        private readonly string _rutaBase;

        public AudioService(AppDbContext context)
        {
            _context = context;
            _rutaBase = Path.Combine(AppContext.BaseDirectory, "wwwroot");
        }

        public async Task<List<AudioResponseDto>> GetByInspeccionAsync(int inspeccionId)
        {
            return await _context.Audios
                .Where(a => a.InspeccionId == inspeccionId)
                .Select(a => MapToDto(a))
                .ToListAsync();
        }

        public async Task<AudioResponseDto> UploadAsync(int inspeccionId, IFormFile archivo)
        {
            var carpeta = Path.Combine(_rutaBase, "audios");
            if (!Directory.Exists(carpeta))
                Directory.CreateDirectory(carpeta);

            var nombreArchivo = $"{Guid.NewGuid()}{Path.GetExtension(archivo.FileName)}";
            var rutaCompleta = Path.Combine(carpeta, nombreArchivo);

            using (var stream = new FileStream(rutaCompleta, FileMode.Create))
            {
                await archivo.CopyToAsync(stream);
            }

            var audio = new Audio
            {
                InspeccionId = inspeccionId,
                RutaAudio = $"/audios/{nombreArchivo}",
                FechaRegistro = DateTime.UtcNow
            };

            _context.Audios.Add(audio);
            await _context.SaveChangesAsync();

            return MapToDto(audio);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var audio = await _context.Audios.FindAsync(id);
            if (audio == null) return false;

            var rutaCompleta = Path.Combine(_rutaBase, audio.RutaAudio.TrimStart('/'));
            if (File.Exists(rutaCompleta))
                File.Delete(rutaCompleta);

            _context.Audios.Remove(audio);
            await _context.SaveChangesAsync();
            return true;
        }

        private static AudioResponseDto MapToDto(Audio a) => new()
        {
            Id = a.Id,
            InspeccionId = a.InspeccionId,
            RutaAudio = a.RutaAudio,
            FechaRegistro = a.FechaRegistro
        };
    }
}