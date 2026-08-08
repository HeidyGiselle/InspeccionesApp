namespace InspeccionesApp.Domain.Entities;

public class Fotografia
{
    public int Id { get; set; }
    public int InspeccionId { get; set; }
    public string RutaImagen { get; set; } = string.Empty;
    public DateTime FechaRegistro { get; set; } = DateTime.UtcNow;

    public Inspeccion? Inspeccion { get; set; }
}