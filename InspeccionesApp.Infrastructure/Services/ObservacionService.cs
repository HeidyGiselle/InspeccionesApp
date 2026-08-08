using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using InspeccionesApp.Domain.Entities;
using InspeccionesApp.Infrastructure.Data;
using Microsoft.EntityFrameworkCore;

namespace InspeccionesApp.Infrastructure.Services
{
    public class ObservacionService : IObservacionService
    {
        private readonly AppDbContext _context;

        public ObservacionService(AppDbContext context)
        {
            _context = context;
        }

        public async Task<List<ObservacionResponseDto>> GetByInspeccionAsync(int inspeccionId)
        {
            return await _context.Observaciones
                .Where(o => o.InspeccionId == inspeccionId)
                .Select(o => MapToDto(o))
                .ToListAsync();
        }

        public async Task<ObservacionResponseDto> CreateAsync(ObservacionCreateDto dto)
        {
            var observacion = new Observacion
            {
                InspeccionId = dto.InspeccionId,
                Comentario = dto.Comentario,
                FechaRegistro = DateTime.UtcNow
            };

            _context.Observaciones.Add(observacion);
            await _context.SaveChangesAsync();

            return MapToDto(observacion);
        }

        public async Task<bool> UpdateAsync(int id, string comentario)
        {
            var observacion = await _context.Observaciones.FindAsync(id);
            if (observacion == null) return false;

            observacion.Comentario = comentario;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var observacion = await _context.Observaciones.FindAsync(id);
            if (observacion == null) return false;

            _context.Observaciones.Remove(observacion);
            await _context.SaveChangesAsync();
            return true;
        }

        private static ObservacionResponseDto MapToDto(Observacion o) => new()
        {
            Id = o.Id,
            InspeccionId = o.InspeccionId,
            Comentario = o.Comentario,
            FechaRegistro = o.FechaRegistro
        };
    }
}