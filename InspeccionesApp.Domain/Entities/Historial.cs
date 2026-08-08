namespace InspeccionesApp.Domain.Entities;

public class Historial
{
    public int Id { get; set; }
    public int InspeccionId { get; set; }
    public string Accion { get; set; } = string.Empty;
    public DateTime Fecha { get; set; } = DateTime.UtcNow;
    public string Usuario { get; set; } = string.Empty;

    public Inspeccion? Inspeccion { get; set; }
}