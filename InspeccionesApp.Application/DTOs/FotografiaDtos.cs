namespace InspeccionesApp.Application.DTOs
{
    public class FotografiaResponseDto
    {
        public int Id { get; set; }
        public int InspeccionId { get; set; }
        public string RutaImagen { get; set; } = string.Empty;
        public DateTime FechaRegistro { get; set; }
    }
}