using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using InspeccionesApp.Domain.Entities;
using InspeccionesApp.Infrastructure.Data;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;

namespace InspeccionesApp.Infrastructure.Services
{
    public class FotografiaService : IFotografiaService
    {
        private readonly AppDbContext _context;
        private readonly string _rutaBase;

        public FotografiaService(AppDbContext context)
        {
            _context = context;
            _rutaBase = Path.Combine(AppContext.BaseDirectory, "wwwroot");
        }

        public async Task<List<FotografiaResponseDto>> GetByInspeccionAsync(int inspeccionId)
        {
            return await _context.Fotografias
                .Where(f => f.InspeccionId == inspeccionId)
                .Select(f => MapToDto(f))
                .ToListAsync();
        }

        public async Task<FotografiaResponseDto> UploadAsync(int inspeccionId, IFormFile archivo)
        {
            var carpeta = Path.Combine(_rutaBase, "fotos");
            if (!Directory.Exists(carpeta))
                Directory.CreateDirectory(carpeta);

            var nombreArchivo = $"{Guid.NewGuid()}{Path.GetExtension(archivo.FileName)}";
            var rutaCompleta = Path.Combine(carpeta, nombreArchivo);

            using (var stream = new FileStream(rutaCompleta, FileMode.Create))
            {
                await archivo.CopyToAsync(stream);
            }

            var fotografia = new Fotografia
            {
                InspeccionId = inspeccionId,
                RutaImagen = $"/fotos/{nombreArchivo}",
                FechaRegistro = DateTime.UtcNow
            };

            _context.Fotografias.Add(fotografia);
            await _context.SaveChangesAsync();

            return MapToDto(fotografia);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var fotografia = await _context.Fotografias.FindAsync(id);
            if (fotografia == null) return false;

            var rutaCompleta = Path.Combine(_rutaBase, fotografia.RutaImagen.TrimStart('/'));
            if (File.Exists(rutaCompleta))
                File.Delete(rutaCompleta);

            _context.Fotografias.Remove(fotografia);
            await _context.SaveChangesAsync();
            return true;
        }

        private static FotografiaResponseDto MapToDto(Fotografia f) => new()
        {
            Id = f.Id,
            InspeccionId = f.InspeccionId,
            RutaImagen = f.RutaImagen,
            FechaRegistro = f.FechaRegistro
        };
    }
}