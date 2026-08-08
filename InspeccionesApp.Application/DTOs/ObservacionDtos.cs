namespace InspeccionesApp.Application.DTOs
{
    public class ObservacionCreateDto
    {
        public int InspeccionId { get; set; }
        public string Comentario { get; set; } = string.Empty;
    }

    public class ObservacionResponseDto
    {
        public int Id { get; set; }
        public int InspeccionId { get; set; }
        public string Comentario { get; set; } = string.Empty;
        public DateTime FechaRegistro { get; set; }
    }
}