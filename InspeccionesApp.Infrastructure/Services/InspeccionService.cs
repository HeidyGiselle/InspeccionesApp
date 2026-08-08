using InspeccionesApp.Application.DTOs;
using InspeccionesApp.Application.Interfaces;
using InspeccionesApp.Domain.Entities;
using InspeccionesApp.Infrastructure.Data;
using Microsoft.EntityFrameworkCore;

namespace InspeccionesApp.Infrastructure.Services
{
    public class InspeccionService : IInspeccionService
    {
        private readonly AppDbContext _context;

        public InspeccionService(AppDbContext context)
        {
            _context = context;
        }

        public async Task<List<InspeccionResponseDto>> GetAllAsync(int usuarioId, string rol)
        {
            var query = _context.Inspecciones.AsQueryable();

            if (rol != "Administrador")
                query = query.Where(i => i.UsuarioId == usuarioId);

            return await query.Select(i => MapToDto(i)).ToListAsync();
        }

        public async Task<InspeccionResponseDto?> GetByIdAsync(int id, int usuarioId, string rol)
        {
            var inspeccion = await _context.Inspecciones.FindAsync(id);
            if (inspeccion == null) return null;

            if (rol != "Administrador" && inspeccion.UsuarioId != usuarioId)
                return null;

            return MapToDto(inspeccion);
        }

        public async Task<InspeccionResponseDto> CreateAsync(InspeccionCreateDto dto, int usuarioId)
        {
            var inspeccion = new Inspeccion
            {
                UsuarioId = usuarioId,
                Titulo = dto.Titulo,
                Descripcion = dto.Descripcion,
                FechaInspeccion = dto.FechaInspeccion,
                Latitud = dto.Latitud,
                Longitud = dto.Longitud,
                Estado = "Abierta"
            };

            _context.Inspecciones.Add(inspeccion);
            await _context.SaveChangesAsync();

            await RegistrarHistorial(inspeccion.Id, "Creación", usuarioId);

            return MapToDto(inspeccion);
        }

        public async Task<bool> UpdateAsync(int id, InspeccionUpdateDto dto, int usuarioId, string rol)
        {
            var inspeccion = await _context.Inspecciones.FindAsync(id);
            if (inspeccion == null) return false;

            if (rol != "Administrador" && inspeccion.UsuarioId != usuarioId)
                return false;

            if (rol != "Administrador" && inspeccion.Estado != "Abierta")
                return false;

            inspeccion.Titulo = dto.Titulo;
            inspeccion.Descripcion = dto.Descripcion;
            if (rol == "Administrador")
                inspeccion.Estado = dto.Estado;

            await _context.SaveChangesAsync();
            await RegistrarHistorial(id, "Edición", usuarioId);

            return true;
        }

        public async Task<bool> DeleteAsync(int id, int usuarioId, string rol)
        {
            var inspeccion = await _context.Inspecciones.FindAsync(id);
            if (inspeccion == null) return false;

            if (rol != "Administrador" && inspeccion.UsuarioId != usuarioId)
                return false;

            // Registrar el historial ANTES de borrar la inspección,
            // para no violar la clave foránea (FK_Historial_Inspecciones_InspeccionId)
            await RegistrarHistorial(id, "Eliminación", usuarioId);

            _context.Inspecciones.Remove(inspeccion);
            await _context.SaveChangesAsync();

            return true;
        }

        private async Task RegistrarHistorial(int inspeccionId, string accion, int usuarioId)
        {
            var usuario = await _context.Usuarios.FindAsync(usuarioId);
            _context.Historial.Add(new Historial
            {
                InspeccionId = inspeccionId,
                Accion = accion,
                Fecha = DateTime.UtcNow,
                Usuario = usuario?.Nombre ?? "Desconocido"
            });
            await _context.SaveChangesAsync();
        }

        private static InspeccionResponseDto MapToDto(Inspeccion i) => new()
        {
            Id = i.Id,
            UsuarioId = i.UsuarioId,
            Titulo = i.Titulo,
            Descripcion = i.Descripcion,
            FechaInspeccion = i.FechaInspeccion,
            Latitud = i.Latitud,
            Longitud = i.Longitud,
            Estado = i.Estado
        };
    }
}