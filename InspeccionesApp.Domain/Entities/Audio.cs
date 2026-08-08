namespace InspeccionesApp.Domain.Entities;

public class Audio
{
    public int Id { get; set; }
    public int InspeccionId { get; set; }
    public string RutaAudio { get; set; } = string.Empty;
    public int Duracion { get; set; }
    public DateTime FechaRegistro { get; set; } = DateTime.UtcNow;

    public Inspeccion? Inspeccion { get; set; }
}