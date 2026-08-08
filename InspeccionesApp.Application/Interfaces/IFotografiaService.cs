using InspeccionesApp.Application.DTOs;
using Microsoft.AspNetCore.Http;

namespace InspeccionesApp.Application.Interfaces
{
    public interface IFotografiaService
    {
        Task<List<FotografiaResponseDto>> GetByInspeccionAsync(int inspeccionId);
        Task<FotografiaResponseDto> UploadAsync(int inspeccionId, IFormFile archivo);
        Task<bool> DeleteAsync(int id);
    }
}