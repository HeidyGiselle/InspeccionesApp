using InspeccionesApp.Application.DTOs;

namespace InspeccionesApp.Application.Interfaces
{
    public interface IInspeccionService
    {
        Task<List<InspeccionResponseDto>> GetAllAsync(int usuarioId, string rol);
        Task<InspeccionResponseDto?> GetByIdAsync(int id, int usuarioId, string rol);
        Task<InspeccionResponseDto> CreateAsync(InspeccionCreateDto dto, int usuarioId);
        Task<bool> UpdateAsync(int id, InspeccionUpdateDto dto, int usuarioId, string rol);
        Task<bool> DeleteAsync(int id, int usuarioId, string rol);
    }
}