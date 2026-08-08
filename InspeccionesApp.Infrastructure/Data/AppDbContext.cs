using InspeccionesApp.Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace InspeccionesApp.Infrastructure.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<Usuario> Usuarios { get; set; }
    public DbSet<Inspeccion> Inspecciones { get; set; }
    public DbSet<Fotografia> Fotografias { get; set; }
    public DbSet<Audio> Audios { get; set; }
    public DbSet<Observacion> Observaciones { get; set; }
    public DbSet<Historial> Historial { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Inspeccion>()
            .HasOne(i => i.Usuario)
            .WithMany(u => u.Inspecciones)
            .HasForeignKey(i => i.UsuarioId);

        modelBuilder.Entity<Fotografia>()
            .HasOne(f => f.Inspeccion)
            .WithMany(i => i.Fotografias)
            .HasForeignKey(f => f.InspeccionId);

        modelBuilder.Entity<Audio>()
            .HasOne(a => a.Inspeccion)
            .WithMany(i => i.Audios)
            .HasForeignKey(a => a.InspeccionId);

        modelBuilder.Entity<Observacion>()
            .HasOne(o => o.Inspeccion)
            .WithMany(i => i.Observaciones)
            .HasForeignKey(o => o.InspeccionId);

        modelBuilder.Entity<Historial>()
            .HasOne(h => h.Inspeccion)
            .WithMany(i => i.Historial)
            .HasForeignKey(h => h.InspeccionId);
    }
}