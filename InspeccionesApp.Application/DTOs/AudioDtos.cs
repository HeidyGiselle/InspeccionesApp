namespace InspeccionesApp.Application.DTOs
{
    public class AudioResponseDto
    {
        public int Id { get; set; }
        public int InspeccionId { get; set; }
        public string RutaAudio { get; set; } = string.Empty;
        public DateTime FechaRegistro { get; set; }
    }
}