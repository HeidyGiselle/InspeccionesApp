using InspeccionesApp.Application.DTOs;

namespace InspeccionesApp.Application.Interfaces
{
    public interface IObservacionService
    {
        Task<List<ObservacionResponseDto>> GetByInspeccionAsync(int inspeccionId);
        Task<ObservacionResponseDto> CreateAsync(ObservacionCreateDto dto);
        Task<bool> UpdateAsync(int id, string comentario);
        Task<bool> DeleteAsync(int id);
    }
}