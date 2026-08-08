namespace InspeccionesApp.Domain.Entities;

public class Usuario
{
    public int Id { get; set; }
    public string Nombre { get; set; } = string.Empty;
    public string Correo { get; set; } = string.Empty;
    public string PasswordHash { get; set; } = string.Empty;
    public string Rol { get; set; } = "Inspector";
    public bool Estado { get; set; } = true;

    public ICollection<Inspeccion> Inspecciones { get; set; } = new List<Inspeccion>();
}