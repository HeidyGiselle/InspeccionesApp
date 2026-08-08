using InspeccionesApp.Application.DTOs;
using Microsoft.AspNetCore.Http;

namespace InspeccionesApp.Application.Interfaces
{
    public interface IAudioService
    {
        Task<List<AudioResponseDto>> GetByInspeccionAsync(int inspeccionId);
        Task<AudioResponseDto> UploadAsync(int inspeccionId, IFormFile archivo);
        Task<bool> DeleteAsync(int id);
    }
}